package dutchiepay.backend.global.security;

import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.domain.user.service.AccessTokenBlackListService;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 검증")
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AccessTokenBlackListService accessTokenBlackListService;

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
                    if (accessTokenBlackListService.isTokenBlackListed(token)) {
                        throw new UserErrorException(UserErrorCode.INVALID_ACCESS_TOKEN);
                    }
                    UserDetailsImpl userDetails = getUserDetails(token);
                    setAuthenticationUser(userDetails, request);
                    filterChain.doFilter(request, response);
                    return;
                } else if ("refresh".equals(tokenType)) {
                    Long userId = claims.get("userId", Long.class);

                    //날짜 비교 로직 필요X getUserInfoFromToken -> 만료된 토큰 시 ExpiredJwtException 던짐
                    User user = userRepository.findById(userId).orElseThrow(
                        () -> new UserErrorException(UserErrorCode.USER_NOT_FOUND)
                    );

                    String storedRefreshToken = user.getRefreshToken();

                    // 저장된 리프레시 토큰과 일치하는지 확인
                    if (token.equals(storedRefreshToken)) {
                        // 새 액세스 토큰 발급
                        String newAccessToken = jwtUtil.createAccessToken(userId);
                        response.addHeader("Authorization", "Bearer " + newAccessToken);
                    } else {
                        throw new UserErrorException(UserErrorCode.INVALID_REFRESH_TOKEN);
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

    private UserDetailsImpl getUserDetails(String accessToken) {
        Long userId = jwtUtil.getUserInfoFromToken(accessToken).get("userId", Long.class);
        return (UserDetailsImpl) userDetailsService.loadUserById(userId);
    }

    private void setAuthenticationUser(UserDetailsImpl userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authenticated user: {}", userDetails.getUserId());
    }
}
