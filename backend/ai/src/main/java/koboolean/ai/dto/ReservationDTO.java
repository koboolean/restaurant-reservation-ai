package koboolean.ai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import koboolean.ai.domain.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationDTO(

        @JsonPropertyDescription("예약 ID")
        Long reservationId,

        @JsonPropertyDescription("예약 일시")
        LocalDateTime reservationTime,

        @JsonPropertyDescription("예약 고객 정보")
        CustomerDTO customer,

        @JsonPropertyDescription("예약 테이블 정보")
        RestaurantTableDTO restaurantTable,

        @JsonPropertyDescription("방문 인원")
        int partySize,

        @JsonPropertyDescription("알레르기 정보")
        String allergies,

        @JsonPropertyDescription("예약 상태 (CONFIRMED, CANCELLED, COMPLETED)")
        ReservationStatus status

) {

    public static ReservationDTO from(koboolean.ai.entity.Reservation reservation) {

        return new ReservationDTO(

                reservation.getId(),

                reservation.getReservationTime(),

                new CustomerDTO(
                        true,
                        reservation.getCustomer().getId(),
                        reservation.getCustomer().getPhoneNumber(),
                        reservation.getCustomer().getName(),
                        reservation.getCustomer().getVisitCount(),
                        reservation.getCustomer().getMemo()
                ),

                new RestaurantTableDTO(
                        reservation.getRestaurantTable().getId(),
                        reservation.getRestaurantTable().getCapacity(),
                        reservation.getRestaurantTable().getType()
                ),

                reservation.getPartySize(),

                reservation.getAllergies(),

                reservation.getStatus()
        );
    }
}
