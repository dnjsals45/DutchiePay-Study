package dutchiepay.backend.domain.commerce.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetBuyListResponseDto {
    private List<ProductDto> products;
    private Long cursor;

    public static GetBuyListResponseDto from(List<ProductDto> products, Long cursor) {

        return GetBuyListResponseDto.builder()
                .products(products)
                .cursor(cursor)
                .build();
    }

    @Getter
    @Builder
    public static class ProductDto {
        private Long buyId;
        private String productName;
        private String productImg;
        private Integer productPrice;
        private Integer discountPrice;
        private Integer discountPercent;
        private Integer skeleton;
        private Integer nowCount;
        private Integer expireDate;
        private Boolean isLiked;
        private Double rating;
        private Long reviewCount;
    }
}


