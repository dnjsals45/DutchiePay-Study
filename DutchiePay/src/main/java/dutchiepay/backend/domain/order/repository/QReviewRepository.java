package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.domain.profile.dto.GetMyReviewResponseDto;
import dutchiepay.backend.entity.User;

import java.util.List;

public interface QReviewRepository {
    List<GetMyReviewResponseDto> getMyReviews(User user);
}
