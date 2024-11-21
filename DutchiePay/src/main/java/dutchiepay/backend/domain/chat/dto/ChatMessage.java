package dutchiepay.backend.domain.chat.dto;

import lombok.Getter;

@Getter
public class ChatMessage {
    private Long sender;
    private String content;
}
