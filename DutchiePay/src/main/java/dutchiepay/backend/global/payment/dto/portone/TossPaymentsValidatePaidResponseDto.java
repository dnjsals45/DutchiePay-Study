package dutchiepay.backend.global.payment.dto.portone;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class TossPaymentsValidatePaidResponseDto {

    private String status;
    private String id;  // paymentId
    private String transactionId;
    private String merchantId;
    private String storeId;
    private String orderName;   // product name
    private String currency;
    private InnerPaymentMethod paymentMethod;
    private InnerChannel channel;
    private InnerAmount amount;

    @Getter
    @NoArgsConstructor
    public static class InnerPaymentMethod {
        private String type;

        @Getter
        @NoArgsConstructor
        public static class InnerCard {
            private String name;
            private String number;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class InnerChannel {
        private String pgProvider;
    }


    @Getter
    @NoArgsConstructor
    public static class InnerAmount {
        private BigDecimal total;
        private BigDecimal paid;
    }

}
