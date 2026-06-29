package koboolean.multiai.entity;

import jakarta.persistence.*;
import koboolean.multiai.domain.TableType;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(comment = "테이블")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(comment = "테이블 ID")
    private Long id;

    @Column(comment = "최대 수용인원")
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(comment = "테이블 타입")
    private TableType type;

}
