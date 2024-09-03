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
    private Long count;
    private Long productPrice;
    private Long totalPrice;
    private Long discountPercent;
    private String payment;
    private String deliveryAddress;
    private Long deliveryState;
    private String productImg;
    private String storeName;
}
