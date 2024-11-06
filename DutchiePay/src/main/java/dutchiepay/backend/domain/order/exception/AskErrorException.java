package dutchiepay.backend.domain.order.exception;

import lombok.Getter;

@Getter
public class AskErrorException extends RuntimeException {
    private final AskErrorCode askErrorCode;

    public AskErrorException(AskErrorCode askErrorCode) {
        super(askErrorCode.getMessage());
        this.askErrorCode = askErrorCode;
    }

    @Override
    public String toString() {
        return String.format("AskErrorException(code=%s, message=%s)",
                askErrorCode.name(), askErrorCode.getMessage());
    }
}
