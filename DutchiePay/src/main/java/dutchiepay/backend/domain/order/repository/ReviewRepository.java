package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Review;
import dutchiepay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUser(User user);

    Optional<Review> findByUserAndReviewId(User user, Long reviewId);

    @Modifying
    @Query("update Review r set r.deletedAt = current_timestamp where r = ?1")
    void softDelete(Review review);

    boolean existsByUserAndOrder(User user, Order order);
}
