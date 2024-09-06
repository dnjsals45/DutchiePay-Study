package dutchiepay.backend.global.security;

import dutchiepay.backend.global.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 검증")
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtUtil.getJwtFromHeader(request);

        if (token != null) {
            try {

                //tokenType 확인
                Claims claims = jwtUtil.getUserInfoFromToken(token);
                String tokenType = claims.get("tokenType", String.class);

                if ("access".equals(tokenType)) {
                    filterChain.doFilter(request, response);
                    return;
                } else if ("refresh".equals(tokenType)) {

                    String refreshToken = token;
                    String userId = claims.get("userId", String.class);

                    //날짜 비교 로직 필요X getUserInfoFromToken -> 만료된 토큰 시 ExpiredJwtException 던짐
                    RefreshToken storedRefreshToken = refreshTokenRepository.findByUserId(userId)
                            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 리프레시 토큰을 찾지 못했습니다."));

                    // 저장된 리프레시 토큰과 일치하는지 확인
                    if (storedRefreshToken.getTokenString().equals(refreshToken)) {
                        // 새 액세스 토큰 발급
                        String newAccessToken = jwtUtil.createAccessToken(Long.parseLong(userId));
                        response.addHeader("Authorization", "Bearer " + newAccessToken);
                    } else {
                        throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
                    }
                }

            } catch (ExpiredJwtException e) {
                log.error("토큰이 만료되었습니다: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                log.error("잘못된 토큰입니다: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);


    }
}
