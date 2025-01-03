package dutchiepay.backend.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetChatRoomUsersResponseDto {
    private Long userId;
    private String nickname;
    private String profileImg;
    private Boolean isManager;
}
