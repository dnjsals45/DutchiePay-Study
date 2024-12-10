package dutchiepay.backend.domain.community.service;

import dutchiepay.backend.domain.commerce.repository.FreeRepository;
import dutchiepay.backend.domain.community.dto.HitTrack;
import dutchiepay.backend.domain.community.repository.HitTrackRepository;
import dutchiepay.backend.domain.community.repository.ShareRepository;
import dutchiepay.backend.entity.Free;
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
    private final HitTrackRepository hitTrackRepository;

    @Transactional
    public void increaseHitCount(User user, String type, Long postId) {
        Object post = findPostType(type, postId);
        if (post == null) {
            return;
        }

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
        if (type.equals("free")) {
            return freeRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid post type"));
        } else if (type.equals("share")) {
            return shareRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid post type"));
        } else {
            return null;
        }
    }

    private void incrementHitCount(Object post, String type) {
        if (type.equals("free") && post instanceof Free freePost) {
            freePost.increaseHitCount();
        } else if (type.equals("share") && post instanceof Share sharePost) {
            sharePost.increaseHitCount();
        }
    }
}
