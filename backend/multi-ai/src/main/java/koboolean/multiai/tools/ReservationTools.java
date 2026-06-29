package koboolean.multiai.tools;

import koboolean.multiai.dto.BookingDTOs;
import koboolean.multiai.dto.CustomerDTO;
import koboolean.multiai.dto.ReservationDTO;
import koboolean.multiai.dto.RestaurantTableDTO;
import koboolean.multiai.entity.Customer;
import koboolean.multiai.entity.Reservation;
import koboolean.multiai.entity.RestaurantTable;
import koboolean.multiai.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationTools {

    private final ReservationService service;

    @Tool(description = "고객의 연락처로 방문 이력을 조회합니다. 대화 초기에 고객을 식별하기 위해 사용합니다.")
    public CustomerDTO checkCustomerHistory(BookingDTOs.CustomerCheckRequest request) {

        Customer customer = service.checkCustomer(request.phoneNumber());

        if (customer == null) {
            return CustomerDTO.newCustomer(request.phoneNumber());
        }

        return CustomerDTO.from(customer);
    }

    @Tool(description = "날짜, 인원, 선호 좌석에 맞는 예약 가능한 테이블 목록을 조회합니다.")
    public List<RestaurantTableDTO> searchTables(BookingDTOs.TableSearchRequest request) {

        try {

            List<RestaurantTable> tables = service.findAvailableTables(
                    LocalDateTime.parse(request.dateTime()),
                    request.partySize(),
                    request.preferredType()
            );

            return tables.stream()
                    .map(table -> new RestaurantTableDTO(
                            table.getId(),
                            table.getCapacity(),
                            table.getType()
                    ))
                    .toList();

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "날짜 형식이 올바르지 않습니다. ISO-8601 형식을 사용하세요. 예: 2026-01-01T18:00:00"
            );
        }
    }

    @Tool(description = "최종적으로 예약을 생성합니다.")
    public ReservationDTO bookTable(BookingDTOs.CreateReservationRequest request) {

        Reservation reservation = service.createReservation(
                request.customerName(),
                request.phoneNumber(),
                LocalDateTime.parse(request.dateTime()),
                request.tableId(),
                request.partySize(),
                request.allergies()
        );

        return ReservationDTO.from(reservation);
    }

    @Tool(description = "고객의 예정된 예약을 취소합니다. 예약 취소가 성공하면 true가 반환되며 취소가 실패하면 false가 반환됩니다.")
    public Boolean cancelReservation(BookingDTOs.CancelReservationRequest request) {
        return service.cancelReservation(request.reservationId());
    }

    @Tool(description = "고객의 예약 내역을 조회합니다.")
    public List<ReservationDTO> checkMyBooking(BookingDTOs.MyBookingRequest request){

        List<Reservation> reservations = service.getMyBookings(request.phoneNumber());

        return reservations.stream().map(ReservationDTO::from).toList();

    }

}
