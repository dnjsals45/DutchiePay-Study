package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Review;
import dutchiepay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUser(User user);

    Optional<Review> findByUserAndReviewId(User user, Long reviewId);
}
