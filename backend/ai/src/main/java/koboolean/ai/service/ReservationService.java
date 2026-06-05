package koboolean.ai.service;

import koboolean.ai.domain.ReservationStatus;
import koboolean.ai.domain.TableType;
import koboolean.ai.entity.Customer;
import koboolean.ai.entity.Reservation;
import koboolean.ai.entity.RestaurantTable;
import koboolean.ai.repository.CustomerRepository;
import koboolean.ai.repository.ReservationRepository;
import koboolean.ai.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;

    /**
     * 빈 테이블 찾기
     * @param time 시간
     * @param size 방문인원
     * @param typeStr 테이블타입
     * @return 결과
     */
    @Transactional(readOnly = true)
    public String findAvailableTables(LocalDateTime time, int size, String typeStr){
        // 1. 물리적 조건(인원, 타입)에 맞는 테이블 후보를 조회한다.
        List<RestaurantTable> candidates;

        if(typeStr != null){
            try{
                TableType type = TableType.valueOf(typeStr);
                candidates = restaurantTableRepository.findByCapacityGreaterThanEqualAndType(size,type);
            }catch(IllegalArgumentException e){
                return "잘못된 좌석 타입입니다. WINDOW, ROOM, HALL 중 하나의 타입만 입력이 가능합니다.";
            }
        }else{
            candidates = restaurantTableRepository.findByCapacityGreaterThanEqual(size);
        }

        // 2. 해당 시간에 이미 예약된 테이블 ID 조회
        LocalDateTime checkStartTime = time.minusHours(2);
        LocalDateTime checkEndTime = time.plusHours(2);

        List<Long> bookedIds = reservationRepository.findByBookedTableIds(checkStartTime, checkEndTime);

        // 3. 차집합 연산(전체후보에서 예약된 것을 뺀 빈 테이블)
        List<RestaurantTable> available = candidates.stream().filter(t -> !bookedIds.contains(t.getId()))
                .toList();

        if(available.isEmpty()) return "해당 조건에 맞는 빈 테이블이 존재하지 않습니다.";

        // 4. AI가 읽기 편한 자연어로 반환
        StringBuilder sb = new StringBuilder("예약가능한 테이블 목록 : \n");

        for(RestaurantTable rt : available){
            sb.append(String.format("-[ID:%d]%s타입(%d인석)\n", rt.getId(), rt.getType(), rt.getCapacity()));
        }

        return sb.toString();
    }

    /**
     * 예약 생성 및 CRM 연동
     * @param name 이름
     * @param phone 핸드폰번호
     * @param time 시간
     * @param tableId 테이블ID
     * @param size 방문인원
     * @param allergies 알레르기여부
     * @return 결과
     */
    @Transactional
    public String createReservation(String name, String phone, LocalDateTime time, Long tableId, int size, String allergies) {

        // 테이블 유효성 재검증 (동시성 문제 최소화)
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테이블 ID입니다."));

        // CRM: 고객 조회 또는 신규 생성 (Upsert)
        Customer customer = customerRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {
                    // 신규 고객일 경우 실행되는 로직
                    // 1. 초기 메모 생성: 이번 예약의 알레르기 정보를 평생 메모에 기록
                    String initialMemo = "신규 등록";
                    if (allergies != null && !allergies.equals("없음") && !allergies.equals("None")) {
                        initialMemo = "주의: " + allergies;
                    }
                    // 2. 신규 고객 저장 및 반환
                    return customerRepository.save(Customer.builder()
                            .name(name)
                            .phoneNumber(phone)
                            .visitCount(0)
                            .memo(initialMemo)
                            .build());
                });

        // 방문 횟수 증가 (기존 고객이든 신규 고객이든 +1)
        customer.setVisitCount(customer.getVisitCount() + 1);

        // 예약 정보 저장
        Reservation res = Reservation.builder()
                .customer(customer)
                .restaurantTable(table)
                .reservationTime(time)
                .partySize(size)
                .allergies(allergies)
                .status(ReservationStatus.CONFIRMED) // 확정 상태로 저장
                .build();

        reservationRepository.save(res);

        return String.format("예약이 확정되었습니다! 예약번호 [#%d]. %s님을 %d번 테이블(%s)로 모시겠습니다.",
                res.getId(), name, table.getId(), table.getType());
    }

    /**
     * CRM 조회 : 단순 식별용
     * @param phone 핸드폰번호
     * @return 결과
     */
    @Transactional(readOnly = true)
    public String checkCustomer(String phone) {
        return customerRepository.findByPhoneNumber(phone)
                .map(c -> String.format("기존 고객입니다. 이름: %s, 방문횟수: %d회. (VIP 여부 확인 필요)", c.getName(), c.getVisitCount()))
                        .orElse("신규 고객입니다.");
    }

    /**
     * 예약 취소: 모호성 해결 및 상태 변경
     * @param phoneNumber 핸드폰번호
     * @return 결과
     */
    @Transactional
    public String cancelReservation(String phoneNumber) {

        // 1. 미래의 확정된 예약 조회
        List<Reservation> activeReservations = reservationRepository.findUpcomingReservations(phoneNumber);

        if (activeReservations.isEmpty()) {
            return "해당 번호로 예정된 예약 내역이 없습니다.";
        }

        // 2. 예약이 딱 하나만 있을 경우 -> 즉시 취소 처리
        if (activeReservations.size() == 1) {
            Reservation r = activeReservations.get(0);
            r.setStatus(ReservationStatus.CANCELLED); // 상태 변경 (Soft Delete)

            // JPA Dirty Checking에 의해 트랜잭션 종료 시 자동 Update 됨
            return String.format("예약이 정상적으로 취소되었습니다.\n[취소 내역] 날짜: %s, 인원: %d명", r.getReservationTime(), r.getPartySize());
        }

        // 3. 예약이 여러 개일 경우 -> AI에게 목록을 반환하여 사용자에게 선택하게 함
        StringBuilder sb = new StringBuilder("취소 가능한 예약이 여러 건 있습니다. 날짜를 지정해 주세요:\n");

        for (Reservation r : activeReservations) {
            sb.append(String.format("- [ID:%d] %s (%d명)\n", r.getId(), r.getReservationTime(), r.getPartySize()));
        }
        return sb.toString();
    }
}
