package dutchiepay.backend.domain.user.repository;

import dutchiepay.backend.entity.AccessTokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenBlackListRepository extends JpaRepository<AccessTokenBlackList, Long> {

    boolean existsByToken(String token);
}
