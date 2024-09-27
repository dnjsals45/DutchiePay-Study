package dutchiepay.backend.domain.coupon.repository;

import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UsersCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersCouponRepository extends JpaRepository<UsersCoupon, Long> {
    Long countByUser(User user);

    Long countByUserUserId(Long userId);
}
