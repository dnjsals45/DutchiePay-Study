package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Free;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeRepository extends JpaRepository<Free, Long> {

    Optional<Free> findByFreeIdAndDeletedAtIsNull(Long freeId);
}
