package dutchiepay.backend.global.jwt.redis;

import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisService {

    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;
    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;
    private final RefreshRepository refreshRepository;
    private final AccessRepository accessRepository;

    @Transactional
    public void saveToken(Long userId, String refreshToken) {
        refreshRepository.save(RefreshToken.builder()
                .userId(userId)
                .refresh(refreshToken)
                .expiration(refreshTokenExpiration)
                .build());
    }

    @Transactional
    public void addBlackList(Long userId, String accessToken) {
        accessRepository.save(ATBlackList.builder()
                .userId(userId)
                .access(accessToken)
                .expiration(accessTokenExpiration)
                .build());
    }

    public boolean isTokenBlackListed(String accessToken) {
        return accessRepository.findByAccess(accessToken) != null;
    }

    public String getRefreshToken(Long userId) {

        return refreshRepository.findById(userId)
                .orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND))
                .getRefresh();
    }

    public Long findUserIdFromRefreshToken(String refresh) {
        RefreshToken refreshToken = refreshRepository.findByRefresh(refresh)
                .orElseThrow(() -> new UserErrorException(UserErrorCode.INVALID_REFRESH_TOKEN));
        return refreshToken.getUserId();
    }

    @Transactional
    public void deleteRefreshToken(String refresh) {
        refreshRepository.delete(refreshRepository.findByRefresh(refresh)
                .orElseThrow(() -> new UserErrorException(UserErrorCode.INVALID_REFRESH_TOKEN)));
    }
}
