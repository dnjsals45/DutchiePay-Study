package dutchiepay.backend.domain.community.repository;

import dutchiepay.backend.domain.community.dto.GetMartListResponseDto;
import dutchiepay.backend.domain.community.dto.GetMartResponseDto;
import dutchiepay.backend.domain.community.dto.GetMartUpdateResponseDto;
import dutchiepay.backend.domain.community.dto.GetUserCompleteRecentDealsDto;
import dutchiepay.backend.entity.User;

import java.util.List;

public interface QShareRepository {
    GetMartListResponseDto getMartList(User user, String category, Long cursor, Integer limit);

    GetMartResponseDto getMartByShareId(Long shareId);

    List<GetUserCompleteRecentDealsDto> getUserCompleteRecentDeals(Long userId);

    GetMartUpdateResponseDto getMartByShareIdForUpdate(Long shareId);
}
