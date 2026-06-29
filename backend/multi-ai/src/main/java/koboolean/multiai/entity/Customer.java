package koboolean.multiai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(comment = "고객")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(comment = "고객 ID")
    private Long id;

    @Column(unique = true, comment = "핸드폰 번호")
    private String phoneNumber;

    @Column(comment = "이름")
    private String name;

    @Column(comment = "방문횟수")
    private int visitCount;

    @Column(comment = "특이사항")
    private String memo;


}
