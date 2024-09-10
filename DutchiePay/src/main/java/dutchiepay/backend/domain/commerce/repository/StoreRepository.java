package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
