package dutchiepay.backend.domain.community.dto;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Getter
@RedisHash(value = "hitTrack", timeToLive = 60 * 1)
public class HitTrack {
    @Id
    private String id;

    private Long userId;
    private Long postId;
    private String type;
}
