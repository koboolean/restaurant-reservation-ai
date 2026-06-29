package koboolean.multiai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(comment = "메뉴")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(comment = "메뉴 ID")
    private Long id;

    @Column(comment = "메뉴명")
    private String name;

    @Column(comment = "가격")
    private int price;

    @Column(comment = "메뉴 카테고리")
    private String category;

}
