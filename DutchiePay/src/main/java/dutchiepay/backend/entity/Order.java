package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private String phone;

    @Column(length = 5, nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String address;

    private String detail;

    private String message;

    @Column(nullable = false)
    private int totalPrice;

    @Column(length = 15, nullable = false)
    private String payment;

    @Column
    private String tid;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Column(length = 11, nullable = false)
    private String orderNum;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private int quantity;

    @Column
    private LocalDate statusChangeDate;

    public void readyPurchase(String tid) {
        this.tid = tid;
        this.state = "결제준비";
        this.statusChangeDate = LocalDate.now();
    }

    public void changeStatus(String state) {
        this.state = state;
        this.statusChangeDate = LocalDate.now();
    }
}
