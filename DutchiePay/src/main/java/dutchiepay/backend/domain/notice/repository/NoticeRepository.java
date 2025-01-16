package dutchiepay.backend.domain.notice.repository;

import dutchiepay.backend.entity.Notice;
import dutchiepay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long>, QNoticeRepository {
    List<Notice> findByUserAndIsReadFalseAndCreatedAtAfter(User user, LocalDateTime minus);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notice n WHERE n.user = :user AND n.isRead = false AND n.createdAt >= :time AND n.deletedAt IS NULL")
    boolean existUnreadNotification(User user, LocalDateTime time);

    @Transactional
    @Modifying
    @Query("UPDATE Notice n SET n.isRead = true, n.deletedAt = CURRENT_TIMESTAMP WHERE n.user = :user AND n.isRead = false")
    void readAllNotices(User user);
}
