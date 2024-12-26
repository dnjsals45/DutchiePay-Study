package dutchiepay.backend.domain.community.repository;

import dutchiepay.backend.entity.Free;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeRepository extends JpaRepository<Free, Long>, QFreeRepository {

    Optional<Free> findByFreeIdAndDeletedAtIsNull(Long freeId);
}
