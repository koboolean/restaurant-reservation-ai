package koboolean.multiai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import koboolean.multiai.domain.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
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
        ReservationStatus status,

        @JsonPropertyDescription("기념일, 유아 동반 여부 등을 적는 메모장")
        String specialRequests

) {

}
