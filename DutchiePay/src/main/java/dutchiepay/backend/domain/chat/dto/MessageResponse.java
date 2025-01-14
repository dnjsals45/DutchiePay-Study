package dutchiepay.backend.domain.chat.dto;

import dutchiepay.backend.entity.Message;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageResponse {
    private Long messageId;
    private Long senderId;
    private String type;
    private String content;
    private Integer unreadCount;
    private String date;
    private String time;

    public static MessageResponse of(Message message) {
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSenderId())
                .type(message.getType())
                .content(message.getContent())
                .unreadCount(message.getUnreadCount())
                .date(message.getDate())
                .time(message.getTime())
                .build();
    }
}
