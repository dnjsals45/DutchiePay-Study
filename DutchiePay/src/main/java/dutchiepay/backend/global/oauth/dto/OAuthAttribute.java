package dutchiepay.backend.global.oauth.dto;

import dutchiepay.backend.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Random;

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
            case "naver" -> ofNaver(userNameAttributeName, attributes);
            default -> null;
        };
    }

    public static OAuthAttribute ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuthAttribute.builder()
                .email(kakaoAccount.get("email").toString())
                .nickname(generateNickname())
                .oauthId(attributes.get("id").toString())
                .oauthProvider("kakao")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttribute ofNaver(String userNameAttributeName, Map<String, Object> attributes) {

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttribute.builder()
                .email(response.get("email").toString())
                .nickname(generateNickname())
                .oauthId(response.get("id").toString())
                .oauthProvider("naver")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .location("서울시 중구")
                .state(0)
                .oauthId(oauthId)
                .oauthProvider(oauthProvider)
                .build();
    }

    private static String generateNickname() {
        // 닉네임 자동생성
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int randNum = random.nextInt(999999);
        StringBuilder sb = new StringBuilder(Integer.toString(randNum));
        while (sb.length() < 6) {
            sb.insert(0, 0);
        }
        return "더취" + sb;
    }
}
