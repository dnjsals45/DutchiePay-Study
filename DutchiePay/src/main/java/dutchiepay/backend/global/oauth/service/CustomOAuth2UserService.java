package dutchiepay.backend.global.oauth.service;

import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.oauth.dto.OAuthAttribute;
import dutchiepay.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final UserService userService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttribute oAuthAttribute = OAuthAttribute.of(userNameAttributeName, oAuth2User.getAttributes(), registrationId);

        User user = saveOrUpdate(oAuthAttribute, oAuth2User.getAttributes());

        return new UserDetailsImpl(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuthAttribute oAuthAttribute, Map<String, Object> attributes) {
        User user = userRepository.findByOauthProviderAndEmail(oAuthAttribute.getOauthProvider(), oAuthAttribute.getEmail())
                .orElse(oAuthAttribute.toEntity());
        if (user.getState() == 2) {
            if (oAuthAttribute.getOauthProvider().equals("kakao")) {
                userService.unlinkKakao(new UserDetailsImpl(user, attributes));
            }
            else {
                userService.unlinkNaver(new UserDetailsImpl(user, attributes));
            }
            throw new UserErrorException(UserErrorCode.USER_NOT_FOUND);
        }

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
