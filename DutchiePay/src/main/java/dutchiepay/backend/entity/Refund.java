package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "Refund")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long refundId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Long orderId;

    @Column(length = 500, nullable = false)
    private String reason;

    @Column
    private String detail;

    @Column(length = 10, nullable = false)
    private String category;

}
