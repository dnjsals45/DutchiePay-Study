package dutchiepay.backend.domain.main.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProductsAndRecommendsDto {

    private Long buyId;
    private String productName;
    private String productImg;
    private int productPrice;
    private int discountPrice;
    private int discountPercent;
    private LocalDate expireDate;
}
