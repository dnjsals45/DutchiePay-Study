package dutchiepay.backend.domain.user.repository;

import dutchiepay.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    Optional<User> findByOauthProviderAndEmail(String oauthProvider, String email);

    Optional<User> findByNickname(String username);

    Optional<User> findByEmailAndOauthProviderIsNull(String email);

    Optional<User> findByPhoneAndOauthProviderIsNull(String phone);

    Optional<User> findByEmailAndPhoneAndOauthProviderIsNull(String email, String phone);

    boolean existsByNickname(String nickname);
    
    boolean existsByPhoneAndOauthProviderIsNull(String phone);
}
