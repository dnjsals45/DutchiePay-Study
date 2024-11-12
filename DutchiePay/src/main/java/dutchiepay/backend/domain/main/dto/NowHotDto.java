package dutchiepay.backend.domain.main.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class NowHotDto {

    private Long buyId;
    private String productName;
    private String productImg;
    private Integer productPrice;
    private int discountPrice;
    private int discountPercent;
    private int skeleton;
    private int nowCount;
    private LocalDate expireDate;
}
