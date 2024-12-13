package dutchiepay.backend.domain.community.service;

import dutchiepay.backend.domain.community.dto.GetUserCompleteRecentDealsDto;
import dutchiepay.backend.domain.community.repository.ShareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final ShareRepository shareRepository;
    public List<GetUserCompleteRecentDealsDto> getUserCompleteRecentDeals(Long userId) {
        return shareRepository.getUserCompleteRecentDeals(userId);
    }
}
