package koboolean.multiai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import koboolean.multiai.domain.TableType;

public record RestaurantTableDTO(
        @JsonPropertyDescription("테이블 ID")
        Long id,

        @JsonPropertyDescription("최대 수용 인원")
        int capacity,

        @JsonPropertyDescription("테이블 타입(WINDOW, ROOM, HALL)")
        TableType type
){
}
