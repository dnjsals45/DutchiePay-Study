package dutchiepay.backend.domain.delivery.exception;

import lombok.Getter;

@Getter
public class DeliveryErrorException extends RuntimeException{
    private final DeliveryErrorCode deliveryErrorCode;

    public DeliveryErrorException(DeliveryErrorCode deliveryErrorCode){
        super(deliveryErrorCode.getMessage());
        this.deliveryErrorCode = deliveryErrorCode;
    }

    @Override
    public String toString(){
        return String.format("DeliveryErrorException(code=%s, message=%s)",
                deliveryErrorCode.name(), deliveryErrorCode.getMessage());
    }
}
