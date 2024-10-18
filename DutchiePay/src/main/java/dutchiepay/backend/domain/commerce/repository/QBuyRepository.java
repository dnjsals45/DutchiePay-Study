package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.domain.commerce.dto.GetBuyListResponseDto;
import dutchiepay.backend.domain.commerce.dto.GetBuyResponseDto;
import dutchiepay.backend.entity.User;

public interface QBuyRepository {
    GetBuyResponseDto getBuyPageByBuyId(Long userId, Long buyId);

    GetBuyListResponseDto getBuyList(User user, String filter, String category, int end, Long cursor, int limit);
}
