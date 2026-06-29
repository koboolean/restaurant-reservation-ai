package koboolean.multiai.service;

import koboolean.multiai.dto.BookingDTOs;
import koboolean.multiai.entity.Menu;
import koboolean.multiai.entity.OrderItem;
import koboolean.multiai.entity.Reservation;
import koboolean.multiai.repo.MenuRepository;
import koboolean.multiai.repo.OrderItemRepository;
import koboolean.multiai.repo.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 메뉴 정보를 가져온다.
     * @return
     */
    public List<Menu> getMenuBoard(){
        return menuRepository.findAll();
    }

    /**
     * 총 금액을 계산한다.
     * @param orderItems
     * @return
     */
    public int calculateEstimate(List<BookingDTOs.OrderItemRequest> orderItems){
        int total = 0;

        for(BookingDTOs.OrderItemRequest item : orderItems){
            Menu menu = menuRepository.findById(item.menuId()).orElse(null);

            if(menu == null){
                continue;
            }

            int itemTotal = menu.getPrice() * item.quantity();
            total += itemTotal;
        }

        return total;
    }

    /**
     * 주문을 추가한다.
     * @param reservationId
     * @param items
     * @return
     */
    @Transactional
    public Boolean addOrderToReservation(Long reservationId, List<BookingDTOs.OrderItemRequest> items){
        Reservation res = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        for(BookingDTOs.OrderItemRequest item : items){
            Menu menu = menuRepository.findById(item.menuId()).orElse(null);

            if(menu != null){
                OrderItem order = OrderItem.builder()
                        .reservation(res)
                        .menu(menu)
                        .quantity(item.quantity())
                        .request(item.request())
                        .build();

                orderItemRepository.save(order);

                return true;
            }
        }

        return false;
    }

    /**
     * 주문내역 조회
     * @param reservationId
     * @return 결과
     */
    public List<OrderItem> getOrderHistory(Long reservationId){
        if(!reservationRepository.existsById(reservationId)){
            throw new IllegalArgumentException("예약 내역이 존재하지 않습니다.");
        }

        return orderItemRepository.findByReservationId(reservationId);
    }

    /**
     * 전체 주문 취소
     * @param reservationId
     * @return 결과
     */
    @Transactional
    public Boolean cancelOrder(Long reservationId){
        if(!orderItemRepository.existsByReservationId(reservationId)){
            throw new IllegalArgumentException("예약 내역이 존재하지 않습니다.");
        }

        orderItemRepository.deleteByReservationId(reservationId);

        return true;
    }

    /**
     * 특정 메뉴를 취소한다.
     * @param reservationId
     * @param menuName
     * @return
     */
    @Transactional
    public Boolean removeMenuItem(Long reservationId, String menuName){
        List<OrderItem> items = orderItemRepository.findByReservationIdAndMenu_Name(reservationId, menuName);

        if(items.isEmpty()){
            return false;
        }

        orderItemRepository.deleteAll(items);

        return true;
    }

}
