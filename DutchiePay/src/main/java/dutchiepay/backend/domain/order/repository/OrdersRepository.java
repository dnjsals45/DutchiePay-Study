package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Order, Long>, QOrderRepository {
    Optional<Order> findByOrderNum(String orderNum);
}
