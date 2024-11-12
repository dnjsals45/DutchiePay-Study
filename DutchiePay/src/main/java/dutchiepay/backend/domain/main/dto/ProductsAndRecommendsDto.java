package dutchiepay.backend.domain.main.dto;

import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProductsAndRecommendsDto {

    private Long buyId;
    private String productName;
    private String productImg;
    private int productPrice;
    private int discountPrice;
    private int discountPercent;
    private LocalDate expireDate;

    public static ProductsAndRecommendsDto toDto(Buy buy, Product product) {
        return ProductsAndRecommendsDto.builder()
                .buyId(buy.getBuyId())
                .productName(product.getProductName())
                .productImg(product.getProductImg())
                .productPrice(product.getOriginalPrice())
                .discountPrice(product.getSalePrice())
                .discountPercent(product.getDiscountPercent())
                .expireDate(buy.getDeadline())
                .build();
    }
}
