package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long>, QOrderRepository {
    Optional<Orders> findByOrderNum(String orderNum);
}
