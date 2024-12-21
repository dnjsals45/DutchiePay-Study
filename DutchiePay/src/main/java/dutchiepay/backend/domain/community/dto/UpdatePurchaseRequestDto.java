package dutchiepay.backend.domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdatePurchaseRequestDto {

    @NotNull(message = "게시글을 식별할 수 없습니다.")
    private Long purchaseId;
    @NotBlank(message = "제목이 입력되지 않았습니다.")
    private String title;
    private String content;
    private Integer price;
    private String meetingPlace;
    private String latitude;
    private String longitude;
    private String goods;
    private String thumbnail;
    private List<String> images;
    @NotBlank(message = "카테고리가 지정되지 않았습니다.")
    private String category;
}

