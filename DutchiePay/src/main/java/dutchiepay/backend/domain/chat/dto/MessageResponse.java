package dutchiepay.backend.domain.chat.dto;

import dutchiepay.backend.entity.Message;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageResponse {
    private Long messageId;
    private Long sender;
    private String content;
    private Integer unreadCount;

    public static MessageResponse of(Message message) {
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .sender(message.getSenderId())
                .content(message.getContent())
                .unreadCount(message.getUnreadCount())
                .build();
    }
}
