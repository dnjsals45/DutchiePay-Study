package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
