package koboolean.multiai.repo;

import koboolean.multiai.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select r.restaurantTable.id
              from Reservation r
             where r.reservationTime < :endTime
               and r.reservationTime > :startTimeMinus2Hours
               and r.status = 'CONFIRMED'
            """)
    List<Long> findByBookedTableIds(
            @Param("startTimeMinus2Hours") LocalDateTime startTimeMinus2Hours,
            @Param("endTime") LocalDateTime endTime
    ) ;

    @Query(
            """
            select r
              from Reservation r
              join fetch r.customer
              join fetch r.restaurantTable
             where r.customer.phoneNumber = :phoneNumber
               and r.reservationTime > CURRENT_TIMESTAMP
               and r.status = 'CONFIRMED'
            """
    )
    List<Reservation> findUpcomingReservations(@Param("phoneNumber") String phoneNumber);
}
