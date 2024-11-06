package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.domain.profile.dto.GetMyAskResponseDto;
import dutchiepay.backend.entity.User;

import java.util.List;

public interface QAskRepository {
    List<GetMyAskResponseDto> getMyAsks(User user);
}
