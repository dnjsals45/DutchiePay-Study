package dutchiepay.backend.domain.user.dto;

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
public class UserReLoginResponseDto {

    Long userId;
    String type;
    String nickname;
    String profileImg;
    String location;
    String access;
    Boolean isCertified;

    public static UserReLoginResponseDto toDto(final User user, String access,
        Boolean isCertified) {
        return UserReLoginResponseDto.builder()
            .userId(user.getUserId())
            .type(user.getOauthProvider())
            .nickname(user.getNickname())
            .profileImg(user.getProfileImg())
            .location(user.getLocation())
            .access(access)
            .isCertified(isCertified)
            .build();
    }
}
