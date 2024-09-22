package dutchiepay.backend.domain.user.repository;

import dutchiepay.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByOauthProviderAndEmail(String oauthId, String email);

    Optional<User> findByNickname(String username);

    Optional<User> findByEmailAndOauthProviderIsNull(String email);
}
