package dutchiepay.backend.domain.community.repository;

import dutchiepay.backend.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share, Long> {
}
