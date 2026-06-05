package koboolean.ai.domain;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public enum TableType {

    @JsonPropertyDescription(
            "창문 근처 좌석. 전망이나 야경을 선호하는 경우 선택"
    )
    WINDOW,

    @JsonPropertyDescription(
            "독립된 룸 형태의 좌석. 프라이버시와 조용한 환경을 원하는 경우 선택"
    )
    ROOM,

    @JsonPropertyDescription(
            "일반 홀 좌석. 별도의 룸이 아닌 공개된 식사 공간"
    )
    HALL
}
