package dutchiepay.backend.global.config;

import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.domain.user.service.AccessTokenBlackListService;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.oauth.handler.CustomOAuth2SuccessHandler;
import dutchiepay.backend.global.security.JwtAuthenticationEntryPoint;
import dutchiepay.backend.global.security.JwtAuthenticationFilter;
import dutchiepay.backend.global.security.JwtVerificationFilter;
//import dutchiepay.backend.global.security.NicknameQueryParamFilter;
import dutchiepay.backend.global.security.UserDetailsServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AccessTokenBlackListService accessTokenBlackListService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Value("${spring.cors.allowed-origins}")
    private List<String> corsOrigins;

    private final String[] permitAllUrl = {
        "/users/login",
        "/users/signup",
        "/users/pwd",
        "/users/pwd-nonuser",
        "/users/auth",
        "/users/test",
        "/users/relogin",
        "/users/reissue",
        "/users/email",
        "/oauth/signup",
        "/oauth",
        "/image",
        "/health"
    };

    private final String[] readOnlyUrl = {
        "/favicon.ico",
        "/api-docs/**",
        "/users",
        "/v3/api-docs/**", "/swagger-ui/**", "/swagger",
    };

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

    //jwt 검증
    @Bean
    public JwtVerificationFilter jwtVerificationFilter() {
        return new JwtVerificationFilter(jwtUtil, userRepository, userDetailsService,
            accessTokenBlackListService);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .cors(cors -> corsConfigurationSource())
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.GET, readOnlyUrl).permitAll()
                    .requestMatchers(permitAllUrl).permitAll()
                    .anyRequest().authenticated())
            .oauth2Login(oauth2 ->
                oauth2.successHandler(customOAuth2SuccessHandler))
//            .addFilterBefore(new NicknameQueryParamFilter(),
//                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtVerificationFilter(), JwtAuthenticationFilter.class)
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil, userRepository, passwordEncoder()),
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception ->
                exception
                    .authenticationEntryPoint(authenticationEntryPoint()));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsOrigins);
        configuration.setAllowedMethods(
            List.of("GET", "POST", "OPTIONS", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setMaxAge(3600L);
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Access-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
