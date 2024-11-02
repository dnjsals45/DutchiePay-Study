package dutchiepay.backend.global.jwt.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefresh(String refresh);
}
