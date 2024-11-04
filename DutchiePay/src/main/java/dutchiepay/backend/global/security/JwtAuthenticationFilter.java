package dutchiepay.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dutchiepay.backend.domain.user.dto.UserLoginRequestDto;
import dutchiepay.backend.domain.user.dto.UserLoginResponseDto;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.redis.RedisService;
import dutchiepay.backend.global.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j(topic = "로그인 & JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository,
                                   PasswordEncoder passwordEncoder, RedisService redisService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/users/login", "POST"));
    }

    //로그인 시도
    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException {
        try {
            UserLoginRequestDto requestDto = new ObjectMapper().readValue(
                request.getInputStream(),
                UserLoginRequestDto.class
            );

            User user = userRepository.findByEmailAndOauthProviderIsNull(requestDto.getEmail())
                .orElseThrow(
                    () -> new UserErrorException(UserErrorCode.USER_NOT_FOUND)
                );

            if (1 == user.getState()) {
                throw new UserErrorException(UserErrorCode.USER_SUSPENDED);
            } else if (2 == user.getState()) {
                throw new UserErrorException(UserErrorCode.USER_NOT_FOUND);
            }

            if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
                throw new UserErrorException(UserErrorCode.USER_NOT_FOUND);
            }

            // 인증 성공 시 UsernamePasswordAuthenticationToken 생성 및 반환
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // 예시로 ROLE_USER 권한 추가

            return new UsernamePasswordAuthenticationToken(
                new UserDetailsImpl(user), // principal
                user.getPassword(), // credentials
                authorities // authorities
            );

        } catch (UserErrorException e) {
            log.error(e.getMessage());

            try {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ObjectNode json = new ObjectMapper().createObjectNode();
                json.put("message", e.getMessage());
                String newResponse = new ObjectMapper().writeValueAsString(json);

                response.setContentType("application/json");
                response.setContentLength(newResponse.getBytes(StandardCharsets.UTF_8).length);
                response.getOutputStream().write(newResponse.getBytes(StandardCharsets.UTF_8));
            } catch (IOException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }
        return null;
    }

    //로그인 성공
    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult
    ) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        redisService.saveToken(userDetails.getUserId(), refreshToken);
        userRepository.save(user);

        sendResponse(response, UserLoginResponseDto.toDto(user, accessToken, refreshToken));

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                userDetails, null, null
            )
        );
    }

    private void sendResponse(HttpServletResponse response,
        UserLoginResponseDto userLoginResponseDto) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.valueToTree(userLoginResponseDto);

        objectMapper.writeValue(response.getWriter(), rootNode);
    }
}
