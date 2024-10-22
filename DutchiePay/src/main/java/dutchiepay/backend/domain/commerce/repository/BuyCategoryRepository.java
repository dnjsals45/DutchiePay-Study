package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.BuyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyCategoryRepository extends JpaRepository<BuyCategory, Long> {
}
