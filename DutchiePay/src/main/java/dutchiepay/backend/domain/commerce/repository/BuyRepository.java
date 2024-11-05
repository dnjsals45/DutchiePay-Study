package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyRepository extends JpaRepository<Buy, Long>, QBuyRepository {
    Optional<Buy> findByProduct(Product product);
}
