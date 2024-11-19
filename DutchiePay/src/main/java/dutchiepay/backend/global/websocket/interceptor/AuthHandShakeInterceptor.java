package dutchiepay.backend.global.websocket.interceptor;

import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.jwt.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthHandShakeInterceptor implements HandshakeInterceptor {
    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = getJwtFromHeader(request);
        if (token == null || !isValidToken(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 401 상태 코드 설정
            return false;
        }
        attributes.put("userId", jwtUtil.getUserInfoFromToken(token).get("userId", Long.class));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private String getJwtFromHeader(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isValidToken(String token) {
        if ("access".equals(token) && redisService.isTokenBlackListed(token)) {
            return false;
        }
        return true;
    }
}
