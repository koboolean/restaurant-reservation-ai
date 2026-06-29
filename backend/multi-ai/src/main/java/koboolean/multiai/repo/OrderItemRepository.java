package koboolean.multiai.repo;

import koboolean.multiai.entity.Menu;
import koboolean.multiai.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteByReservationId(Long reservationId);

    List<OrderItem> findByReservationId(Long reservationId);

    boolean existsByReservationId(Long reservationId);

    List<OrderItem> findByReservationIdAndMenu_Name(Long reservationId, String menuName);

    String menu(Menu menu);
}
