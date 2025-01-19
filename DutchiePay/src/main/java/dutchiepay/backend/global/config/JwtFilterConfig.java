package dutchiepay.backend.global.config;

import dutchiepay.backend.domain.notice.service.NoticeUtilService;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.jwt.redis.RedisService;
import dutchiepay.backend.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class JwtFilterConfig {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final NoticeUtilService noticeUtilService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userRepository, passwordEncoder, redisService, noticeUtilService);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }
}
