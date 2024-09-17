package dutchiepay.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dutchiepay.backend.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponseDto {

    private Long userId;
    private String type;
    private String nickname;
    private String profileImg;
    private String location;
    private String access;
    private String refresh;
    @JsonProperty("isCertified")
    private boolean certified;

    public static UserLoginResponseDto toDto(User user, String access) {
        return UserLoginResponseDto.builder()
                .userId(user.getUserId())
                .type(user.getOauthProvider())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .location(user.getLocation())
                .access(access)
                .refresh(user.getRefreshToken())
                .certified(user.getPhone() != null).build();
    }
}
