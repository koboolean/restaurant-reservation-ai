package koboolean.multiai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import koboolean.multiai.entity.OrderItem;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderItemDTO(
        @JsonPropertyDescription("주문 ID") Long id,
        @JsonPropertyDescription("고객정보") ReservationDTO reservation,
        @JsonPropertyDescription("메뉴정보") MenuDTO menu,
        @JsonPropertyDescription("수량") int quantity,
        @JsonPropertyDescription("요청사항") String request
){
    public static OrderItemDTO from(OrderItem orderItem) {

        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .reservation(ReservationDTO.from(orderItem.getReservation()))
                .menu(MenuDTO.from(orderItem.getMenu()))
                .quantity(orderItem.getQuantity())
                .request(orderItem.getRequest())
                .build();
    }
}
