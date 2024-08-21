package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "Orders")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long buyPostId;

    @Column(nullable = false)
    private Long couponId;

    @Column(nullable = false)
    private String address;

    @Column
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
}
