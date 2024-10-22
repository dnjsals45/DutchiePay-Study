package dutchiepay.backend.domain.profile.dto;

import dutchiepay.backend.domain.commerce.BuyCategoryEnum;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyLikesResponseDto {
    private BuyCategoryEnum category;
    private String title;
    private Integer originalPrice;
    private Integer salePrice;
    private Integer discountPercent;
    private String thumbnail;
    private Double average;
    private Integer reviewCount;
    private Integer expireDate;
}
