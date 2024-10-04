package dutchiepay.backend.global.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dutchiepay.backend.domain.user.dto.UserLoginResponseDto;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.oauth.dto.CustomOAuth2User;
import dutchiepay.backend.global.security.JwtAuthenticationFilter;
import dutchiepay.backend.global.security.JwtVerificationFilter;
import dutchiepay.backend.global.security.UserDetailsImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl oAuth2User = (UserDetailsImpl) authentication.getPrincipal();

        log.info("Oauth2 login success: User@{}", oAuth2User.getUserId());

        User user = userRepository.findById(oAuth2User.getUserId()).orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));

        // refresh token 발급 후 저장
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());
        user.createRefreshToken(refreshToken);

        // access token 발급 후 UserLoginResponseDto 호출
        String accessToken = jwtUtil.createAccessToken(user.getUserId());

        String html = """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
        </head>
        <body>
            <script>
            const parentOrigin = window.location.origin.startsWith('http://localhost:') || window.location.origin.startsWith('http://127.0.0.1:')
                ? 'http://localhost:3000'
                : 'https://d2m4bskl88m9ql.cloudfront.net';

            window.opener.postMessage(
                {
                    userId: '%s',
                    type: '%s',
                    nickname: '%s',
                    profileImg: '%s',
                    location: '%s',
                    access: '%s',
                    refresh: '%s',
                    isCertified: %b
                },
                parentOrigin
            );
            window.close();
            </script>
        </body>
        </html>
        """.formatted(
                user.getUserId(),
                user.getOauthProvider(),
                user.getNickname(),
                user.getProfileImg(),
                user.getLocation(),
                accessToken,
                refreshToken,
                user.getPhone() != null
        );

        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(html);

        userRepository.save(user);

    }

    /**
     * JSON 형식으로 response를 보내는 메서드
     * 사용하지 않음
     */
    private void sendResponse(HttpServletResponse response, UserLoginResponseDto userLoginResponseDto) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.valueToTree(userLoginResponseDto);

        objectMapper.writeValue(response.getWriter(), rootNode);
    }

    /**
     * 소셜 로그인 성공 후 cookie에 refresh token을 담는 메서드
     * @param response
     * @param name
     * @param value
     */
    public void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(120);
//        cookie.setSecure(true); // HTTPS에서만 전송
        response.addCookie(cookie);
    }
}
