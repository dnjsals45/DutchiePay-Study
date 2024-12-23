package dutchiepay.backend.domain.profile.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyLikesResponseDto {
    private Long buyId;
    private String[] category;
    private String productName;
    private Integer productPrice;
    private Integer discountPrice;
    private Integer discountPercent;
    private String productImg;
    private Double rating;
    private Integer reviewCount;
    private Integer expireDate;
    private Integer skeleton;
    private Integer nowCount;
}
