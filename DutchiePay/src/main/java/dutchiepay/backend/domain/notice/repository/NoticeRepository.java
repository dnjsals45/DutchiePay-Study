package dutchiepay.backend.domain.notice.repository;

import dutchiepay.backend.entity.Notice;
import dutchiepay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByUserAndIsReadFalseAndCreatedAtAfter(User user, LocalDateTime minus);
}
