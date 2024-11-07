package dutchiepay.backend.global.payment.dto.portone;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TossValidateRequestDto {

    private String paymentId;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal totalAmount;
    private String receiver;
    private String phone;
    private String zipCode;
    private String address;
    private String detail;
    private String message;
}
