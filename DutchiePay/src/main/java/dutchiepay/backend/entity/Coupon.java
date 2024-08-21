package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@Table(name = "Coupon")
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
    private Integer percentage;

    // 조건
    @Column(nullable = false)
    private Integer condition;
}
