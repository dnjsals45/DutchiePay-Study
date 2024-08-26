package dutchiepay.backend.domain.user.repository;

import dutchiepay.backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
