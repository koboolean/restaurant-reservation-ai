package koboolean.ai.entity;

import jakarta.persistence.*;
import koboolean.ai.domain.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(comment = "예약정보")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(comment = "예약일시")
    private LocalDateTime reservationTime;

    @JoinColumn(comment = "고객 정보", name = "customer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @JoinColumn(comment = "테이블 정보", name = "restaurant_table_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RestaurantTable restaurantTable;

    @Column(comment = "방문인원")
    private int partySize;

    @Column(comment = "알레르기 정보")
    private String allergies;

    @Column(comment = "예약상태")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

}
