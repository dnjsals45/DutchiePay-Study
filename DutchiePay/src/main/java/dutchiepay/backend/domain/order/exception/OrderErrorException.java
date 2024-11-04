package dutchiepay.backend.domain.order.exception;

import lombok.Getter;

@Getter
public class OrderErrorException extends RuntimeException {
    private final OrderErrorCode orderErrorCode;

    public OrderErrorException(OrderErrorCode orderErrorCode) {
        super(orderErrorCode.getMessage());
        this.orderErrorCode = orderErrorCode;
    }

    @Override
    public String toString() {
        return String.format("OrdersErrorException(code=%s, message=%s)",
                orderErrorCode.name(), orderErrorCode.getMessage());
    }
}
