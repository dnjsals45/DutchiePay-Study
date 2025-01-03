package dutchiepay.backend.global.websocket.interceptor;

import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.jwt.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthHandShakeInterceptor implements HandshakeInterceptor {
    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    public static final String REFRESH_KEY = "refresh";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = getJwtFromCookies(request);
        if (token == null || !isValidToken(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        Long userId = jwtUtil.getUserInfoFromRefreshToken(token).get("userId", Long.class);

        Principal principal = new UsernamePasswordAuthenticationToken(
                userId.toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication((UsernamePasswordAuthenticationToken) principal);

        attributes.put("userId", userId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private String getJwtFromCookies(ServerHttpRequest request) {
        String cookie = request.getHeaders().getFirst("Cookie");
        if (StringUtils.hasText(cookie)) {
            String[] cookies = cookie.split(";");
            for (String c : cookies) {
                String[] keyValue = c.split("=");
                if (keyValue[0].trim().equals(REFRESH_KEY)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    private boolean isValidToken(String token) {
        if ("refresh".equals(token) && redisService.isTokenBlackListed(token)) {
            return false;
        }
        return true;
    }
}
