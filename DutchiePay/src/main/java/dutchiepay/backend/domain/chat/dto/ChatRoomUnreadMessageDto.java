package dutchiepay.backend.domain.chat.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomUnreadMessageDto {
    private Long chatRoomId;
    private Long unreadCount;
    private String message;
}
