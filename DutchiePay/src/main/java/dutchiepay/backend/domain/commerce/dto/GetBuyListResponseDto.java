package dutchiepay.backend.domain.commerce.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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

        public ProductDto(Long buyId, String productName, String productImg, Integer productPrice, Integer discountPrice,
                          Integer discountPercent, Integer skeleton, Integer nowCount, LocalDate deadline, Boolean isLiked) {
            this.buyId = buyId;
            this.productName = productName;
            this.productImg = productImg;
            this.productPrice = productPrice;
            this.discountPrice = discountPrice;
            this.discountPercent = discountPercent;
            this.skeleton = skeleton;
            this.nowCount = nowCount;
            this.expireDate = calculateExpireDate(deadline);
            this.isLiked = isLiked;
        }

        private int calculateExpireDate(LocalDate deadline) {
            if (deadline.isBefore(LocalDate.now())) {
                return -1;
            } else if (deadline.isEqual(LocalDate.now())) {
                return 0;
            } else {
                return (int) ChronoUnit.DAYS.between(LocalDate.now(), deadline);
            }
        }
    }
}


