package dutchiepay.backend.domain.commerce.repository;

import com.querydsl.core.types.OrderSpecifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderAndSortCondition {
    private final OrderSpecifier<?>[] orderBy;
}
