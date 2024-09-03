package dutchiepay.backend.global.oauth.dto;

import dutchiepay.backend.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
public class OAuthAttribute {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private Long userId;
    private String email;
    private String nickname;
    private String oauthId;
    private String oauthProvider;

    @Builder
    public OAuthAttribute(Map<String, Object> attributes, String nameAttributeKey, Long userId, String email, String nickname, String oauthId, String oauthProvider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
    }

    public static OAuthAttribute of(String userNameAttributeName, Map<String, Object> attributes, String registrationId) {
        return switch (registrationId) {
            case "kakao" -> ofKakao(userNameAttributeName, attributes);
            default -> null;
        };
    }

    public static OAuthAttribute ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuthAttribute.builder()
                .email(kakaoAccount.get("email").toString())
                .nickname("test")
                .oauthId(attributes.get("id").toString())
                .oauthProvider("KAKAO")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        // 닉네임 자동생성 필요?

        return User.builder()
                .email(email)
                .username(nickname)
                .nickname(nickname)
                .location("서울시 중구")
                .state(0)
                .oauthId(oauthId)
                .oauthProvider(oauthProvider)
                .build();
    }
}
