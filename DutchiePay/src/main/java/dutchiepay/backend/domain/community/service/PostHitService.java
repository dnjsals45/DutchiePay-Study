package dutchiepay.backend.domain.community.service;

import dutchiepay.backend.domain.community.repository.FreeRepository;
import dutchiepay.backend.domain.community.dto.HitTrack;
import dutchiepay.backend.domain.community.exception.CommunityErrorCode;
import dutchiepay.backend.domain.community.exception.CommunityException;
import dutchiepay.backend.domain.community.repository.HitTrackRepository;
import dutchiepay.backend.domain.community.repository.PurchaseRepository;
import dutchiepay.backend.domain.community.repository.ShareRepository;
import dutchiepay.backend.entity.Free;
import dutchiepay.backend.entity.Purchase;
import dutchiepay.backend.entity.Share;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostHitService {
    private final FreeRepository freeRepository;
    private final ShareRepository shareRepository;
    private final PurchaseRepository purchaseRepository;
    private final HitTrackRepository hitTrackRepository;

    @Transactional
    public void increaseHitCount(User user, String type, Long postId) {
        Object post = findPostType(type, postId);

        String hitTrackKey = user.getUserId() + "_" + postId + "_" + type;

        if (!hitTrackRepository.existsById(hitTrackKey)) {
            incrementHitCount(post, type);

            HitTrack hitTrack = HitTrack.builder()
                    .id(hitTrackKey)
                    .userId(user.getUserId())
                    .postId(postId)
                    .type(type)
                    .build();

            hitTrackRepository.save(hitTrack);
        }
    }

    private Object findPostType(String type, Long postId) {
        return switch (type) {
            case "free" -> freeRepository.findById(postId)
                    .orElseThrow(() -> new CommunityException(CommunityErrorCode.INVALID_POST));
            case "share" -> shareRepository.findById(postId)
                    .orElseThrow(() -> new CommunityException(CommunityErrorCode.INVALID_POST));
            case "purchase" -> purchaseRepository.findById(postId)
                    .orElseThrow(() -> new CommunityException(CommunityErrorCode.INVALID_POST));
            default -> throw new CommunityException(CommunityErrorCode.INVALID_POST_TYPE);
        };
    }

    private void incrementHitCount(Object post, String type) {
        if (type.equals("free") && post instanceof Free freePost) {
            freePost.increaseHitCount();
        } else if (type.equals("share") && post instanceof Share sharePost) {
            sharePost.increaseHitCount();
        } else if (type.equals("purchase") && post instanceof Purchase purchasePost) {
            purchasePost.increaseHitCount();
        }
    }
}
