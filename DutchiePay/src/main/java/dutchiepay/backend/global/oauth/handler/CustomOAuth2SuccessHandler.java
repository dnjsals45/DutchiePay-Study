package dutchiepay.backend.global.oauth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dutchiepay.backend.domain.user.dto.UserLoginResponseDto;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.jwt.redis.RedisService;
import dutchiepay.backend.global.oauth.dto.OAuthSuccessDto;
import dutchiepay.backend.global.security.UserDetailsImpl;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private static String ENCRYPT_SECRET_KEY;
    private static String ALGORITHM;

    @Value("${ENCRYPT_SECRET_KEY}")
    private String tempEncryptSecretKey;

    @Value("${ALGORITHM}")
    private String tempAlgorithm;

    @PostConstruct
    public void init() {
        ENCRYPT_SECRET_KEY = tempEncryptSecretKey;
        ALGORITHM = tempAlgorithm;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetailsImpl oAuth2User = (UserDetailsImpl) authentication.getPrincipal();

        log.info("Oauth2 login success: User@{}", oAuth2User.getUserId());

        User user = userRepository.findById(oAuth2User.getUserId()).orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));

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

        userRepository.save(user);
    }

}
