package dutchiepay.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Buy_Category")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buyCategoryId;

    @ManyToOne
    @JoinColumn(name = "buy_id")
    private Buy buy;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
