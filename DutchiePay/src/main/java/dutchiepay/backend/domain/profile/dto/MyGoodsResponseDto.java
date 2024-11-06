package dutchiepay.backend.domain.profile.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyGoodsResponseDto {
    private Long orderId;
    private String orderNum;
    private Long buyId;
    private LocalDate orderDate;
    private String productName;
    private Integer quantity;
    private Integer productPrice;
    private Integer discountPercent;
    private Integer totalPrice;
    private String payment;
    private String address;
    private String zipCode;
    private String detail;
    private String phone;
    private String deliveryState;
    private String productImg;
    private String storeName;
    private String message;
}
