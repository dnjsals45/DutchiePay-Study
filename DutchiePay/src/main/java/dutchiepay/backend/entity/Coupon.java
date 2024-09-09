package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Table(name = "Coupon")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    // 쿠폰 이름
    @Column(nullable = false, length = 20)
    private String couponName;

    // 유효 기간
    @Column(nullable = false)
    private LocalDate expireDate;

    // 할인율
    @Column(nullable = false)
    private int percentage;

    // 조건
    @Column(nullable = false)
    private int requirePrice;
}
