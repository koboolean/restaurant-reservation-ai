package koboolean.multiai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Builder;

@Builder
public record MenuDTO(
        @JsonPropertyDescription("메뉴 ID") Long id,
        @JsonPropertyDescription("메뉴명") String name,
        @JsonPropertyDescription("가격") int price,
        @JsonPropertyDescription("메뉴 카테고리") String category
) {
}
