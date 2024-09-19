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

        // 유저 데이터 기반으로 토큰 발급
        // 사용자한테 토큰 정보가 없으면 access, refresh 발급, refresh가 유효하고 access가 무효하면 access 발급, 둘다 무효하면 둘다 발급
        User user = userRepository.findById(oAuth2User.getUserId()).orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));

        String accessToken = jwtUtil.createAccessToken(oAuth2User.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(oAuth2User.getUserId());

        user.createRefreshToken(refreshToken);
        userRepository.save(user);

        sendResponse(response, UserLoginResponseDto.toDto(user, accessToken));
    }

    private void sendResponse(HttpServletResponse response, UserLoginResponseDto userLoginResponseDto) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.valueToTree(userLoginResponseDto);

        objectMapper.writeValue(response.getWriter(), rootNode);
    }
}
