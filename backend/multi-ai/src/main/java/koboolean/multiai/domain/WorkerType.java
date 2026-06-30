package koboolean.multiai.domain;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public enum WorkerType{
    @JsonPropertyDescription("예약을 담당하는 타입")
    RESERVATION,
    @JsonPropertyDescription("메뉴 설명 및 주문을 담당하는 타입")
    SOMMELIER,
    @JsonPropertyDescription("장을 방문하는 고객에게 위치, 주차, 영업시간 등 편의 정보를 친절하고 우아하게 안내하는 타입")
    CONCIERGE
}
