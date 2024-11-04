package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Ask;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AskRepository extends JpaRepository<Ask, Long> {

    @Modifying
    @Query("update Ask a set a.deletedAt = current_timestamp where a = ?1")
    void softDelete(Ask ask);

    List<Ask> findAllByUser(User user);

    List<Ask> findByBuyAndDeletedAtIsNull(Buy buy, Pageable pageable);
}
