package dutchiepay.backend.global.config;

import dutchiepay.backend.global.oauth.handler.CustomOAuth2SuccessHandler;
import dutchiepay.backend.global.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2UserService customOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(customOAuth2SuccessHandler)
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOauth2UserService)));

        return httpSecurity.build();
    }
}
