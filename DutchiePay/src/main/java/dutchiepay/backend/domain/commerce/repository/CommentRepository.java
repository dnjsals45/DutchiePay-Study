package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
