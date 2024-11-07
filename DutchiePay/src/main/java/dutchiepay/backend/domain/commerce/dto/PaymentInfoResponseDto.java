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
    private String storeName;
    private LocalDate expireDate;

    public static PaymentInfoResponseDto toDto(Buy buy, String storeName) {
        return PaymentInfoResponseDto.builder()
                .productName(buy.getProduct().getProductName())
                .productImg(buy.getProduct().getProductImg())
                .originalPrice(buy.getProduct().getOriginalPrice())
                .salePrice(buy.getProduct().getSalePrice())
                .storeName(storeName)
                .expireDate(buy.getDeadline())
                .build();
    }
}
