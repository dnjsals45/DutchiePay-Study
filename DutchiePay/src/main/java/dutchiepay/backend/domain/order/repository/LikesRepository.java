package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Likes;
import dutchiepay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    List<Likes> findAllByUser(User user);
}
