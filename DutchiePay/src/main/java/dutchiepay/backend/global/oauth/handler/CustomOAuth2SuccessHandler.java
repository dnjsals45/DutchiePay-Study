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
import jakarta.annotation.PostConstruct;
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
    private final CustomOAuth2UserService customOAuth2UserService;
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

        String encryptedData;
        try {
            encryptedData = encrypt(new ObjectMapper().writeValueAsString(dto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"ko\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <script>\n" +
                "    window.opener.postMessage({type: \"OAUTH_LOGIN\", encrypted: \"" + encryptedData + "\"}, \"https://www.dutchie-pay.site\");\n" +
                "    window.close();\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(html);

        userRepository.save(user);
    }

    /**
     * response 값 암호화
     * @param data 암호화 할 html
     * @return 암호화된 text
     * @throws Exception 암호화 중 발생하는 exception
     */
    public static String encrypt(String data) throws Exception {
        if (ENCRYPT_SECRET_KEY == null || ALGORITHM == null) {
            throw new IllegalStateException("암호화 과정 중 예외 발생");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPT_SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch(Exception e) {
            throw new Exception("암호화 과정 중 예외 발생", e);
        }
    }

}
