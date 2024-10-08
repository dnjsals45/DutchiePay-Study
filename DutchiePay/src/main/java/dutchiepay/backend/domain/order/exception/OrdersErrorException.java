package dutchiepay.backend.domain.order.exception;

import lombok.Getter;

@Getter
public class OrdersErrorException extends RuntimeException {
    private final OrdersErrorCode ordersErrorCode;

    public OrdersErrorException(OrdersErrorCode ordersErrorCode) {
        super(ordersErrorCode.getMessage());
        this.ordersErrorCode = ordersErrorCode;
    }

    @Override
    public String toString() {
        return String.format("OrdersErrorException(code=%s, message=%s)",
                ordersErrorCode.name(), ordersErrorCode.getMessage());
    }
}
