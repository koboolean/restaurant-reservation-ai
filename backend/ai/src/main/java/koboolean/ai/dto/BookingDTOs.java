package koboolean.ai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class BookingDTOs {

    public record CustomerCheckRequest(
            @JsonPropertyDescription("고객식별을 위한 휴대폰 번호(010-XXXX-XXXX)")
            String phoneNumber
    ){}

    public record TableSearchRequest(
            @JsonPropertyDescription("예약 희망 날짜 및 시간(ISO-8601, 예: 2026-01-01T01:00:00)")
            String dateTime,

            @JsonPropertyDescription("방문 인원수")
            Integer partySize,

            @JsonPropertyDescription("선호 좌석 타입(WINDOW, ROOM, HALL), 상관없으면 null")
            String preferredType
    ){}

    public record CreateReservationRequest(
            @JsonPropertyDescription("고객 성함")
            String customerName,
            @JsonPropertyDescription("고객 연락처")
            String phoneNumber,
            @JsonPropertyDescription("확정된 예약시간")
            String dateTime,
            @JsonPropertyDescription("선택한 테이블 번호 (검색된 ID 중 하나)")
            Long tableId,
            @JsonPropertyDescription("인원 수")
            Integer partySize,
            @JsonPropertyDescription("알레르기 정보(없으면 '없음')")
            String allergies
    ){}

    public record CancelReservationRequest(
            @JsonPropertyDescription("예약 ID")
            Long reservationId
    ){}

    public record MyBookingRequest(
            @JsonPropertyDescription("예약자 연락처 (010-xxxx-xxxx)")
            String phoneNumber
    ){}
}
