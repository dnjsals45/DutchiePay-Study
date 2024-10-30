package dutchiepay.backend.global.jwt.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRepository extends CrudRepository<ATBlackList, Long> {
    ATBlackList findByAccess(String accessToken);
}
