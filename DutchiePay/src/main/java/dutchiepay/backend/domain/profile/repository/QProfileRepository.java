package dutchiepay.backend.domain.profile.repository;

import dutchiepay.backend.domain.profile.dto.GetMyLikesResponseDto;
import dutchiepay.backend.entity.User;

import java.util.List;

public interface QProfileRepository {
    List<GetMyLikesResponseDto> getMyLike(User user, String category);
}
