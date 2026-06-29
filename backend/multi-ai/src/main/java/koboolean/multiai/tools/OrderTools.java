package koboolean.multiai.tools;

import koboolean.multiai.dto.BookingDTOs;
import koboolean.multiai.dto.MenuDTO;
import koboolean.multiai.dto.OrderItemDTO;
import koboolean.multiai.entity.Menu;
import koboolean.multiai.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderTools {

    private final OrderService orderService;
    private final VectorStore vectorStore;

    @Tool(description = "전체 메뉴판을 보여줍니다.")
    public List<MenuDTO> showMenuList(){

        List<Menu> menuBoard = orderService.getMenuBoard();

        return menuBoard.stream().map(MenuDTO::from).toList();
    }

    @Tool(description = "주문 예상 견적을 계산합니다.")
    public int getPriceEstimate(BookingDTOs.EstimateRequest request){
        return orderService.calculateEstimate(request.orderItems());
    }

    @Tool(description = "예약에 메뉴 주문을 추가합니다. 정상적으로 주문이 완료되었을 경우 true를 반환합니다.")
    public Boolean addOrder(BookingDTOs.AddOrderRequest addOrderRequest){
        return orderService.addOrderToReservation(addOrderRequest.reservationId(), addOrderRequest.orderItems());
    }

    @Tool(description = "주문 내역을 조회합니다.")
    public List<OrderItemDTO> checkOrderedMenu(BookingDTOs.OrderHistoryRequest request){
        return orderService.getOrderHistory(request.reservationId()).stream().map(OrderItemDTO::from).toList();
    }

    @Tool(description = "특정 메뉴 하나를 취소합니다.")
    public Boolean removeMenuItem(BookingDTOs.CancelMenuItemRequest request){
        return orderService.removeMenuItem(request.reservationId(), request.menuName());
    }

    @Tool(description = "전체 주문을 취소합니다.")
    public Boolean cancelOrder(BookingDTOs.CancelOrderRequest request){
        return orderService.cancelOrder(request.reservationId());
    }

    @Tool(description = "고객이 '메뉴 추천'을 원하거나, 특정 맛/재료를 찾을 경우 상세 정보를 검색합니다.")
    public List<Document> searchMenuDescription(String query){
        return this.vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(3).build()
        );
    }

}
