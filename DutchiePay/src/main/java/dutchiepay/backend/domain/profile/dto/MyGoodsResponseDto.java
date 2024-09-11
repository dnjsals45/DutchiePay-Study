package dutchiepay.backend.domain.profile.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyGoodsResponseDto {
    private Long orderId;
    private String orderNum;
    private Long productId;
    private LocalDateTime orderDate;
    private String productName;
    private Integer count;
    private Integer productPrice;
    private Integer totalPrice;
    private Integer discountPercent;
    private String payment;
    private String deliveryAddress;
    private String deliveryState;
    private String productImg;
    private String storeName;
}
