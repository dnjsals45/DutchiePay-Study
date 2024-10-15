package dutchiepay.backend.domain.commerce.exception;

import lombok.Getter;

@Getter
public class CommerceException extends RuntimeException{

    private final CommerceErrorCode commerceErrorCode;

    public CommerceException(CommerceErrorCode commerceErrorCode) {
        super(commerceErrorCode.getMessage());
        this.commerceErrorCode = commerceErrorCode;
    }

    @Override
    public String toString() {
        return String.format("CommerceErrorException(code=%s, message=%s)",
                commerceErrorCode.name(), commerceErrorCode.getMessage());
    }
}
