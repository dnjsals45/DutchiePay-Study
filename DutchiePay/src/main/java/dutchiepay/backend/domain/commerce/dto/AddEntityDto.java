package dutchiepay.backend.domain.commerce.dto;

import dutchiepay.backend.entity.Category;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class AddEntityDto {
    private String productImg;
    private String detailImg;
    private String productName;
    private int originalPrice;
    private int salePrice;
    private int discountPercent;
    private List<String> category;
    private int skeleton;
    private LocalDate deadline;
    private Long storeId;
    private String storeName;
    private String contactNumber;
    private String representative;
    private String storeAddress;
}
