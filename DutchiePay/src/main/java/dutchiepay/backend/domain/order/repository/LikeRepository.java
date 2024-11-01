package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Like;
import dutchiepay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByUser(User user);

    Like findByUserAndBuy(User user, Buy buy);
}
