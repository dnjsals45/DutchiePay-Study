package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    Score findByBuy(Buy buy);
}
