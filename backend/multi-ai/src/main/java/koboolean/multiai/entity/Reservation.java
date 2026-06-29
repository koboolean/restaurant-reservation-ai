package koboolean.multiai.entity;

import jakarta.persistence.*;
import koboolean.multiai.domain.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(comment = "예약")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(comment = "예약 ID")
    private Long id;

    @Column(comment = "예약일시")
    private LocalDateTime reservationTime;

    @JoinColumn(comment = "고객 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @JoinColumn(comment = "테이블 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    private RestaurantTable restaurantTable;

    @Column(comment = "방문인원")
    private int partySize;

    @Column(comment = "알레르기 정보")
    private String allergies;

    @Column(comment = "예약상태")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(comment = "기념일, 유아 동반 여부 등을 적는 메모장")
    private String specialRequests;

}
