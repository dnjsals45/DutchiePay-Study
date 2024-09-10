package dutchiepay.backend.domain.profile.repository;

import dutchiepay.backend.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Likes,Long>, QProfileRepository {
}
