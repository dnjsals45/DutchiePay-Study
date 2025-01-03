package dutchiepay.backend.domain.chat.exception;

import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {

    private final ChatErrorCode chatErrorCode;

    public ChatException(ChatErrorCode chatErrorCode) {
        super(chatErrorCode.getMessage());
        this.chatErrorCode = chatErrorCode;
    }

    @Override
    public String toString() {
        return String.format("ChatErrorException(code=%s, message=%s)",
                chatErrorCode.name(), chatErrorCode.getMessage());
    }
}
