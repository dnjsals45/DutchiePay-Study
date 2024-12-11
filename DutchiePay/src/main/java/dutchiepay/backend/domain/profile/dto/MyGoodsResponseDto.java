package dutchiepay.backend.domain.profile.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyGoodsResponseDto {
    private List<Goods> goods;
    private Boolean hasNext;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Goods {
        private Long orderId;
        private String orderNum;
        private Long buyId;
        private LocalDate orderDate;
        private String productName;
        private Integer quantity;
        private Integer productPrice;
        private Integer discountPrice;
        private Integer totalPrice;
        private String payment;
        private String receiver;
        private String address;
        private String zipCode;
        private String detail;
        private String phone;
        private String deliveryState;
        private String productImg;
        private String storeName;
        private String message;
    }
}
