package dutchiepay.backend.domain.notice.repository;

import dutchiepay.backend.entity.Notice;
import dutchiepay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByUserAndIsReadFalseAndCreatedAtAfter(User user, LocalDateTime minus);

    @Query("SELECT n FROM Notice n WHERE n.user = :user AND n.createdAt >= :time AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<Notice> findRecentNotices(User user, LocalDateTime time);
}
