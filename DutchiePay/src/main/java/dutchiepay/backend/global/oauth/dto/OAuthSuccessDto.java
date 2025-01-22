package dutchiepay.backend.global.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthSuccessDto {

    private Long userId;
    private String type;
    private String nickname;
    private String profileImg;
    private String location;
    private String access;
    private String refresh;
    private Boolean isCertified;
}
