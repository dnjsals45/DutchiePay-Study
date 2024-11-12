package dutchiepay.backend.domain.main.dto;

import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class NowHotDto {

    private Long buyId;
    private String productName;
    private String productImg;
    private Integer productPrice;
    private int discountPrice;
    private int discountPercent;
    private int skeleton;
    private int nowCount;
    private LocalDate expireDate;

    public static NowHotDto toDto(Buy buy, Product product) {
        return NowHotDto.builder()
                .buyId(buy.getBuyId())
                .productName(product.getProductName())
                .productImg(product.getProductImg())
                .productPrice(product.getOriginalPrice())
                .discountPrice(product.getSalePrice())
                .discountPercent(product.getDiscountPercent())
                .skeleton(buy.getSkeleton())
                .nowCount(buy.getNowCount())
                .expireDate(buy.getDeadline())
                .build();
    }
}
