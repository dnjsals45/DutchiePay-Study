package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Users_Coupon")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users_Coupon extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User users;

    @ManyToOne
    @JoinColumn(name = "couponId")
    private Coupon coupon;

}