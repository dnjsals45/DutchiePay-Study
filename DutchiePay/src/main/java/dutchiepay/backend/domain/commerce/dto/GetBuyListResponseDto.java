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
        private Long buyPostId;
        private String productName;
        private String productImg;
        private int productPrice;
        private int discountPrice;
        private int discountPercent;
        private int skeleton;
        private int nowCount;
        private int expireDate;
        private boolean isLiked;
    }
}


