package dutchiepay.backend.domain.community.repository;

import dutchiepay.backend.entity.Share;
import dutchiepay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShareRepository extends JpaRepository<Share, Long>, QShareRepository {
    @Modifying
    @Query("UPDATE Share s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.shareId = :shareId")
    void softDelete(Long shareId);

    boolean existsByShareIdAndUser(Long shareId, User user);
}
