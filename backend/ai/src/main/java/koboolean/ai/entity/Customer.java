package koboolean.ai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(comment = "예약자정보")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, comment = "핸드폰 번호")
    private String phoneNumber;

    @Column(comment = "예약자명")
    private String name;

    @Column(comment = "방문인원")
    @Setter
    private int visitCount;

    @Column(comment = "메모정보")
    private String memo;


}
