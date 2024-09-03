package dutchiepay.backend.global.oauth.service;

import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.oauth.dto.CustomOAuth2User;
import dutchiepay.backend.global.oauth.dto.OAuthAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttribute oAuthAttribute = OAuthAttribute.of(userNameAttributeName, oAuth2User.getAttributes(), registrationId);

        User user = saveOrUpdate(oAuthAttribute);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuthAttribute.getAttributes(),
                oAuthAttribute.getNameAttributeKey(),
                user.getUserId(),
                user.getOauthId(),
                user.getOauthProvider()
        );
    }

    private User saveOrUpdate(OAuthAttribute oAuthAttribute) {
        User user = userRepository.findByOauthId(oAuthAttribute.getEmail())
                .orElse(oAuthAttribute.toEntity());

        if (!user.getOauthProvider().equals(oAuthAttribute.getOauthProvider())) {
            User otherAccount = User.builder()
                    .email(oAuthAttribute.getEmail())
                    .nickname(user.getNickname())
                    .oauthId(oAuthAttribute.getOauthId())
                    .oauthProvider(oAuthAttribute.getOauthProvider())
                    .build();
            return userRepository.save(otherAccount);
        }

        return userRepository.save(user);
    }
}
