package dutchiepay.backend.domain.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReissueResponseDto {

    String access;

    public static UserReissueResponseDto toDto(String access) {
        return UserReissueResponseDto.builder()
            .access(access)
            .build();
    }
}
