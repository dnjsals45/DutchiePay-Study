package dutchiepay.backend.domain.user.repository;

import dutchiepay.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByNickname(String username);
}
