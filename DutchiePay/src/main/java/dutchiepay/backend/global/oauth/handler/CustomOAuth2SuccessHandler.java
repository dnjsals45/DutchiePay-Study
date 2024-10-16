package dutchiepay.backend.global.oauth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dutchiepay.backend.domain.user.dto.UserLoginResponseDto;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.JwtUtil;
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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl oAuth2User = (UserDetailsImpl) authentication.getPrincipal();

        log.info("Oauth2 login success: User@{}", oAuth2User.getUserId());

        User user = userRepository.findById(oAuth2User.getUserId()).orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));

        // refresh token 발급 후 저장
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());
        user.createRefreshToken(refreshToken);

        String accessToken = jwtUtil.createAccessToken(user.getUserId());

        String data = "   \"userId\": " + user.getUserId() + ",\n" +
                "   \"type\": " + user.getOauthProvider() + ",\n" +
                "   \"nickname\": "+ user.getNickname() + ",\n" +
                "   \"profileImg\": "  + user.getProfileImg() + ",\n" +
                "   \"location\": "+ user.getLocation() + ",\n" +
                "   \"access\": "+ accessToken + ",\n" +
                "   \"refresh\": "+ refreshToken + ",\n" +
                "   \"isCertified\": " + (user.getPhone() != null);

        String encryptedData;
        try {
            encryptedData = encrypt(data);
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
