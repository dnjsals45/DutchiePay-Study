package dutchiepay.backend.domain.profile.repository;

import dutchiepay.backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Like,Long>, QProfileRepository {
}
