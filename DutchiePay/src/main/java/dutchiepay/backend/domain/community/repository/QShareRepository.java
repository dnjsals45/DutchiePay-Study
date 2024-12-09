package dutchiepay.backend.domain.community.repository;

import dutchiepay.backend.domain.community.dto.GetMartListResponseDto;
import dutchiepay.backend.domain.community.dto.GetMartResponseDto;
import dutchiepay.backend.entity.User;

public interface QShareRepository {
    GetMartListResponseDto getMartList(User user, String category, Long cursor, Integer limit);

    GetMartResponseDto getMartByShareId(Long shareId);
}
