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
    public List<RestaurantTable> findAvailableTables(LocalDateTime time, int size, String typeStr) {

        List<RestaurantTable> candidates;

        if (typeStr != null) {
            TableType type = TableType.valueOf(typeStr);
            candidates = restaurantTableRepository
                    .findByCapacityGreaterThanEqualAndType(size, type);
        } else {
            candidates = restaurantTableRepository
                    .findByCapacityGreaterThanEqual(size);
        }

        LocalDateTime checkStartTime = time.minusHours(2);
        LocalDateTime checkEndTime = time.plusHours(2);

        List<Long> bookedIds =
                reservationRepository.findByBookedTableIds(checkStartTime, checkEndTime);

        return candidates.stream()
                .filter(t -> !bookedIds.contains(t.getId()))
                .toList();
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
    public Reservation createReservation(
            String name,
            String phone,
            LocalDateTime time,
            Long tableId,
            int size,
            String allergies) {

        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테이블 ID입니다."));

        Customer customer = customerRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {

                    String initialMemo = "신규 등록";

                    if (allergies != null &&
                            !"없음".equals(allergies) &&
                            !"None".equalsIgnoreCase(allergies)) {
                        initialMemo = "주의: " + allergies;
                    }

                    return customerRepository.save(
                            Customer.builder()
                                    .name(name)
                                    .phoneNumber(phone)
                                    .visitCount(0)
                                    .memo(initialMemo)
                                    .build()
                    );
                });

        customer.setVisitCount(customer.getVisitCount() + 1);

        Reservation reservation = Reservation.builder()
                .customer(customer)
                .restaurantTable(table)
                .reservationTime(time)
                .partySize(size)
                .allergies(allergies)
                .status(ReservationStatus.CONFIRMED)
                .build();

        return reservationRepository.save(reservation);
    }

    /**
     * CRM 조회 : 단순 식별용
     * @param phone 핸드폰번호
     * @return 결과
     */
    @Transactional(readOnly = true)
    public Customer checkCustomer(String phone) {

        return customerRepository.findByPhoneNumber(phone)
                .orElse(null);
    }

    /**
     * 예약 취소: 모호성 해결 및 상태 변경
     * @param reservationId 예약번호
     * @return 결과
     */
    @Transactional
    public Boolean cancelReservation(Long reservationId) {

        Reservation activeReservation = reservationRepository.findById(reservationId)
                .orElse(null);

        if (activeReservation != null) {
            activeReservation.setStatus(ReservationStatus.CANCELLED);

            return true;
        }

        return false;
    }

    /**
     * 예약 확인
     * @param phoneNumber 핸드폰번호
     * @return
     */
    @Transactional(readOnly = true)
    public List<Reservation> getMyBookings(String phoneNumber){
        return reservationRepository.findUpcomingReservations(phoneNumber);
    }
}
