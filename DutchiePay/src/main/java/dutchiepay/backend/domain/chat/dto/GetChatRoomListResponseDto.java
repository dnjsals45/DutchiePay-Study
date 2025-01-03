package dutchiepay.backend.domain.chat.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetChatRoomListResponseDto {
    private Long chatRoomId;
    private String chatName;
    private String chatImg;
    private Integer chatUser;
    private Integer unreadCount;
    private String lastMsg;
    private String lastChatTime;
    private String type;
}
