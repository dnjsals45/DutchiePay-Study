package dutchiepay.backend.domain.search.repository;

import dutchiepay.backend.domain.commerce.dto.GetBuyListResponseDto;
import dutchiepay.backend.entity.User;

public interface QSearchRepository {

    GetBuyListResponseDto searchCommerce(User user, String filter, String keyword, int end, Long cursor, int limit);
}
