package dutchiepay.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dutchiepay.backend.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Boolean hasNotice;

    public static UserLoginResponseDto toDto(User user, String access, String refresh, Boolean hasNotice) {
        return UserLoginResponseDto.builder()
            .userId(user.getUserId())
            .type(user.getOauthProvider())
            .nickname(user.getNickname())
            .profileImg(user.getProfileImg())
            .location(user.getLocation())
            .access(access)
            .refresh(refresh)
            .hasNotice(hasNotice)
            .certified(user.getPhone() != null).build();
    }
}
