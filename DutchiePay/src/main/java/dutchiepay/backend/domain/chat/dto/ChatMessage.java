package dutchiepay.backend.domain.chat.dto;

import lombok.Getter;

@Getter
public class ChatMessage {
    private String type;
    private Long senderId;
    private String content;
    private String date;
    private String time;
}
