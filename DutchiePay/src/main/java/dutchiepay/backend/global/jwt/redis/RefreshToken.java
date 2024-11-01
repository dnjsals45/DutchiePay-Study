package dutchiepay.backend.global.jwt.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "refresh", timeToLive = 604800000)
public class RefreshToken {
    @Id
    private Long userId;

    @Indexed
    private String refresh;

    @TimeToLive
    private Long expiration;
}
