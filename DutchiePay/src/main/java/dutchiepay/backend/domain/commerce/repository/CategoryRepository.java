package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String category);
}
