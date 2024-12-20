package dutchiepay.backend.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseForUpdateDto {

    private String title;
    private String category;
    private String contents;
    private String goods;
    private Integer price;
    private String meetingPlace;
    private String latitude;
    private String longitude;

}
