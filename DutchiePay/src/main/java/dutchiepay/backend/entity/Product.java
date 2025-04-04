package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Product")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    // 상품 이름
    @Column(nullable = false, length = 100)
    private String productName;

    // 상세 내용
    @Column(nullable = false, length = 500)
    private String detailImg;

    // 원가격
    @Column(nullable = false)
    private int originalPrice;

    // 판매가격
    @Column(nullable = false)
    private int salePrice;

    // 원가대비 할인율
    @Column(nullable = false)
    private int discountPercent;

    // 상품 이미지
    @Column(nullable = false, length = 500)
    private String productImg;
}
