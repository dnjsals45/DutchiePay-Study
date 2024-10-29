package dutchiepay.backend.global.jwt.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;
    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;
    private final RefreshRepository refreshRepository;
    private final AccessRepository accessRepository;

    public void saveToken(Long userId, String refreshToken) {
        refreshRepository.save(RefreshToken.builder()
                .userId(userId)
                .refresh(refreshToken)
                .expiration(refreshTokenExpiration)
                .build());
    }

    public void addBlackList(Long userId, String accessToken) {
        accessRepository.save(ATBlackList.builder()
                .userId(userId)
                .access(accessToken)
                .expiration(accessTokenExpiration)
                .build());
    }
}
