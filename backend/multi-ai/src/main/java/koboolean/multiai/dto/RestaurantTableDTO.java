package koboolean.multiai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import koboolean.multiai.domain.TableType;
import koboolean.multiai.entity.RestaurantTable;
import lombok.Builder;

@Builder
public record RestaurantTableDTO(
        @JsonPropertyDescription("테이블 ID")
        Long id,

        @JsonPropertyDescription("최대 수용 인원")
        int capacity,

        @JsonPropertyDescription("테이블 타입(WINDOW, ROOM, HALL)")
        TableType type
){
    public static RestaurantTableDTO from(RestaurantTable restaurantTable) {
        return RestaurantTableDTO.builder()
                .id(restaurantTable.getId())
                .capacity(restaurantTable.getCapacity())
                .type(restaurantTable.getType())
                .build();
    }
}
