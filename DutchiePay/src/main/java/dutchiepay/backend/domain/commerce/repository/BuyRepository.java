package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Buy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyRepository extends JpaRepository<Buy, Long>, QBuyRepository {
}
