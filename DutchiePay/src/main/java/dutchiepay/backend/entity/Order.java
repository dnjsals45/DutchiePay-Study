package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "Orders")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "buy_id")
    private Buy buy;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(nullable = false)
    private String address;

    private String detail;

    @Column(nullable = false)
    private int totalPrice;

    @Column(length = 15, nullable = false)
    private String payment;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Column(length = 11, nullable = false)
    private String orderNum;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private int amount;

    public void confirmPurchase() {
        this.state = "구매확정";
    }

    public void cancelPurchase() {
        this.state = "주문취소";
    }
}
