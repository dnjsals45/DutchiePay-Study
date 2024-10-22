package dutchiepay.backend.domain.commerce.dto;

import dutchiepay.backend.domain.commerce.BuyCategoryEnum;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AddEntityDto {
    private String productImg;
    private String detailImg;
    private String productName;
    private int originalPrice;
    private int salePrice;
    private int discountPercent;
    private BuyCategoryEnum category;
    private int skeleton;
    private LocalDate deadline;
    private Long storeId;
    private String storeName;
    private String contactNumber;
    private String representative;
    private String storeAddress;
}
