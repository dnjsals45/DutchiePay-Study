package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.domain.commerce.dto.GetBuyResponseDto;

public interface QBuyRepository {
    GetBuyResponseDto getBuyPageByBuyId(Long userId, Long buyId);
}
