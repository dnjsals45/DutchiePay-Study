package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, QOrderRepository {
    Optional<Order> findByOrderNum(String orderNum);

    boolean existsByOrderNum(String orderNum);

    Order findByTid(String transactionId);

    List<Order> findAllByState(String inProgress);

    @Query("SELECT o FROM Order o WHERE o.state = :state AND o.buy.deadline < :targetDate")
    List<Order> findPreparingShipmentOrders(String state, LocalDate targetDate);

    @Query("SELECT o FROM Order o WHERE o.state IN :states AND o.statusChangeDate >= :targetDate")
    List<Order> findShippingOrders(List<String> states, LocalDate targetDate);

    @Query("SELECT o FROM Order o WHERE o.state = :state AND o.statusChangeDate >= :targetDate")
    List<Order> findCompletedOrders(String state, LocalDate targetDate);
}
