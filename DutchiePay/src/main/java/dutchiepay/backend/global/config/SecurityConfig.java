package dutchiepay.backend.global.config;

import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.jwt.RefreshTokenRepository;
import dutchiepay.backend.global.security.JwtAuthenticationFilter;
import dutchiepay.backend.global.security.JwtVerificationFilter;
import dutchiepay.backend.global.security.NicknameQueryParamFilter;
import dutchiepay.backend.global.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }
    
    //로그인 및 jwt 생성
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userRepository,
            refreshTokenRepository, passwordEncoder);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    //jwt 검증
    @Bean
    public JwtVerificationFilter jwtVerificationFilter() {
        return new JwtVerificationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                    .requestMatchers("/users/login").permitAll()
                    .requestMatchers("/user/signup").permitAll()
                    .requestMatchers("/users?nickname").permitAll()
                    .requestMatchers("/users/email").permitAll()
                    .requestMatchers("/users/pwd").permitAll()
                    .requestMatchers("/users/pwd-nonuser").permitAll()
                    .requestMatchers("/users/auth").permitAll()
                    .anyRequest().authenticated())
            //TODO 주석제거 corsFilter 작성 후
            //.addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new NicknameQueryParamFilter(),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtVerificationFilter(), JwtAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
