package koboolean.multiai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderItemDTO(
        @JsonPropertyDescription("주문 ID") Long id,
        @JsonPropertyDescription("고객정보") List<ReservationDTO> reservation,
        @JsonPropertyDescription("메뉴정보") List<MenuDTO> menu,
        @JsonPropertyDescription("수량") int quantity,
        @JsonPropertyDescription("요청사항") String request
){}
