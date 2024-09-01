package dutchiepay.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dutchiepay.backend.domain.user.dto.UserLoginRequestDto;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인 & JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository,
        PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        //setFilterProcessUrl("/users/login");
    }

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

            User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new BadCredentialsException("존재하지 않는 이메일입니다.")
            );

            if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("비밀번호가 올바르지 않습니다.");
            }

            return new CustomAuthenticationToken(
                user,
                requestDto.getPassword()
            );

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }
        //TODO 계정이 정지된 경우, 탈퇴한 경우 예외처리 필요?
        /*
        AccountExpiredException: 계정이 만료된 경우
        LockedException: 계정이 잠겨 있는 경우
        DisabledException: 계정이 비활성화된 경우
         */
    }
}
