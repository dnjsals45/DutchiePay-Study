package dutchiepay.backend.domain.coupon.repository;

import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.Users_Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersCouponRepository extends JpaRepository<Users_Coupon, Long> {
    Long countByUser(User user);

    Long countByUserUserId(Long userId);
}
