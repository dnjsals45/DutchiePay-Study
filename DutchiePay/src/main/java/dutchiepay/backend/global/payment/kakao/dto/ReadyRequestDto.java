package dutchiepay.backend.global.payment.kakao.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadyRequestDto {
    private Long buyId;
    private String itemName;
    private Integer quantity;
    private Integer totalAmount;
    private Integer taxFreeAmount;
    private String receiver;
    private String phone;
    private String zipCode;
    private String address;
    private String detail;
    private String message;
}
