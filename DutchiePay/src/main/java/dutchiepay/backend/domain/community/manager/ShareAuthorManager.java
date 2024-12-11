package dutchiepay.backend.domain.community.manager;

import dutchiepay.backend.domain.community.repository.ShareRepository;
import dutchiepay.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShareAuthorManager {
    private final ShareRepository shareRepository;

    public boolean isShareAuthor(Long shareId, UserDetailsImpl userDetails) {
        return shareRepository.existsByShareIdAndUser(shareId, userDetails.getUser());
    }
}
