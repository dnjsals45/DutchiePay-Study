package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.domain.commerce.dto.GetBuyListResponseDto;
import dutchiepay.backend.domain.commerce.dto.GetBuyResponseDto;
import dutchiepay.backend.domain.commerce.dto.GetProductReviewResponseDto;
import dutchiepay.backend.entity.User;
import org.springframework.data.domain.PageRequest;

public interface QBuyRepository {
    GetBuyResponseDto getBuyPageByBuyId(Long userId, Long buyId);

    GetBuyListResponseDto getBuyList(User user, String filter, String category, int end, Long cursor, int limit);

    GetProductReviewResponseDto getProductReview(Long buyId, Long photo, PageRequest pageable);
}
