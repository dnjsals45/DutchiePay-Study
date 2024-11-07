package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, QOrderRepository {
    Optional<Order> findByOrderNum(String orderNum);

    boolean existsByOrderNum(String orderNum);
}
