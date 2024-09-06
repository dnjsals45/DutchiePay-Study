package dutchiepay.backend.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    @Value("${jwt.secret.access.key}")
    private String accessSecretKey;

    @Value("${jwt.secret.refresh.key}")
    private String refreshSecretKey;

    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    private Key accessKey;
    private Key refreshKey;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String BEARER_PREFIX = "Bearer ";

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    //키 초기화
    @PostConstruct
    public void init() {

        byte[] accessKeyBytes = Base64.getDecoder().decode(accessSecretKey);
        accessKey = Keys.hmacShaKeyFor(accessKeyBytes);

        byte[] refreshKeyBytes = Base64.getDecoder().decode(refreshSecretKey);
        refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    //액세스 토큰 생성
    public String createAccessToken(Long userId) {

        Date now = new Date();

        return Jwts.builder()
            .claim("userId", userId)
            .claim("tokenType", "access")
            .setExpiration(new Date(now.getTime() + accessTokenExpiration))
            .signWith(accessKey, signatureAlgorithm)
            .compact();
    }

    //리프레시 토큰 생성
    public String createRefreshToken(Long userId) {

        Date now = new Date();

        return Jwts.builder()
            .claim("userId", userId)
            .claim("tokenType", "refresh")
            .setExpiration(new Date(now.getTime() + refreshTokenExpiration))
            .signWith(refreshKey, signatureAlgorithm)
            .compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKeyFromToken(token)).build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getKeyFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token)
            .getBody();
        String tokenType = claims.get("tokenType", String.class);

        if ("access".equals(tokenType)) {
            return accessKey;
        } else if ("refresh".equals(tokenType)) {
            return refreshKey;
        }
        throw new IllegalArgumentException("토큰 타입이 유효하지 않습니다.");
    }
}
