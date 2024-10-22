package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.domain.commerce.service.CommerceService;
import dutchiepay.backend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByStoreName(String storeName);
}
