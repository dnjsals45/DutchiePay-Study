package dutchiepay.backend.global.jwt.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@RedisHash(value = "blackList", timeToLive = 1800000)
public class ATBlackList{

    @Id
    private Long userId;

    @Indexed
    private String access;

    @TimeToLive
    private Long expiration;
}
