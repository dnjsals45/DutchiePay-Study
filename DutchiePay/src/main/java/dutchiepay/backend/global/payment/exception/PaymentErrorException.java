package dutchiepay.backend.global.payment.exception;

import lombok.Getter;

@Getter
public class PaymentErrorException extends RuntimeException {
    private final PaymentErrorCode paymentErrorCode;

    public PaymentErrorException(PaymentErrorCode paymentErrorCode) {
        super(paymentErrorCode.getMessage());
        this.paymentErrorCode = paymentErrorCode;
    }

    @Override
    public String toString() {
        return String.format("PaymentErrorException(code=%s, message=%s)",
                paymentErrorCode.name(), paymentErrorCode.getMessage());
    }
}
