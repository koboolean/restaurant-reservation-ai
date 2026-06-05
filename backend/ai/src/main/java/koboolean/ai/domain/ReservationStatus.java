package koboolean.ai.domain;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public enum ReservationStatus {

    @JsonPropertyDescription("예약이 정상적으로 접수되어 확정된 상태. 아직 방문 및 식사가 완료되지 않음")
    CONFIRMED,

    @JsonPropertyDescription("고객 또는 매장에서 예약을 취소한 상태. 더 이상 유효하지 않은 예약")
    CANCELLED,

    @JsonPropertyDescription("고객의 방문 및 식사가 모두 완료된 상태. 예약이 정상적으로 종료됨")
    COMPLETED
}
