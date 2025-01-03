package dutchiepay.backend.domain.chat.dto;

import lombok.Getter;

@Getter
public class JoinChatRoomRequestDto {
    private Long postId;
    private String type;
}
