package dutchiepay.backend.domain.profile.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyLikesResponseDto {
    private String[] category;
    private List<Product> products;

    public static class Product {
        private String category;
        private String title;
        private Integer originalPrice;
        private Integer salePrice;
        private Long discountPercent;
        private String thumbnail;
        private Double average;
        private Long reviewCount;
        private Integer expireDate;
    }
}
