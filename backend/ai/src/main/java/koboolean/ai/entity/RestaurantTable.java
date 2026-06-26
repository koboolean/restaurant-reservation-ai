package koboolean.ai.entity;

import jakarta.persistence.*;
import koboolean.ai.domain.TableType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(comment = "레스토랑 테이블")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(comment = "최대 수용인원")
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(comment = "테이블 타입")
    private TableType type;

}
