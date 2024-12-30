package dutchiepay.backend.domain.notice.repository;

import dutchiepay.backend.domain.notice.dto.GetNoticeListResponseDto;
import dutchiepay.backend.entity.User;

import java.util.List;

public interface QNoticeRepository {
    List<GetNoticeListResponseDto> findRecentNotices(User user);

    List<GetNoticeListResponseDto> getMoreNotices(User user, Long noticeId);
}
