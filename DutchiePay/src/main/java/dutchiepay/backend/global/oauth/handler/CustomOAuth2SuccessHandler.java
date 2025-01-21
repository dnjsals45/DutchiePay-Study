package dutchiepay.backend.global.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.jwt.redis.RedisService;
import dutchiepay.backend.global.oauth.dto.OAuthSuccessDto;
import dutchiepay.backend.global.oauth.service.CustomOAuth2UserService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthorizedClientService oauthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetailsImpl oAuth2User = (UserDetailsImpl) authentication.getPrincipal();

        log.info("Oauth2 login success: User@{}", oAuth2User.getUserId());

        User user = userRepository.findById(oAuth2User.getUserId()).orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));

        if (user.getState() == 2) {
            if (user.getOauthProvider().equals("kakao")) {
                customOAuth2UserService.unlinkKakao(oAuth2User);
            }
            else {
                customOAuth2UserService.unlinkNaver(oAuth2User);
            }
            throw new CustomAuthenticationException("해당하는 유저가 없습니다.");
        }

        // refresh token 발급 후 저장
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());
        redisService.saveToken(user.getUserId(), refreshToken);

        String accessToken = jwtUtil.createAccessToken(user.getUserId());

        OAuthSuccessDto dto = OAuthSuccessDto.builder()
                .userId(user.getUserId())
                .type(user.getOauthProvider())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .location(user.getLocation())
                .access(accessToken)
                .refresh(refreshToken)
                .isCertified(user.getPhone() != null).build();

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(dto));

    }

}
