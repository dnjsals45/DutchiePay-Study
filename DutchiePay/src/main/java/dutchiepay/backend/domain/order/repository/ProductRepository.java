package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
