package dutchiepay.backend.domain.commerce.dto;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class OrderAndCursorCondition {
    private OrderSpecifier[] orderBy;
    private BooleanBuilder cursorCondition;
}
