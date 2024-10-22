package dutchiepay.backend.domain.commerce.dto;

import dutchiepay.backend.entity.Buy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PaymentInfoResponseDto {

    private String productName;
    private String productImg;
    private int originalPrice;
    private int salePrice;
    private LocalDate expireDate;

    public static PaymentInfoResponseDto toDto(Buy buy) {
        return PaymentInfoResponseDto.builder()
                .productName(buy.getProductId().getProductName())
                .productImg(buy.getProductId().getProductImg())
                .originalPrice(buy.getProductId().getOriginalPrice())
                .salePrice(buy.getProductId().getSalePrice())
                .expireDate(buy.getDeadline())
                .build();
    }
}
