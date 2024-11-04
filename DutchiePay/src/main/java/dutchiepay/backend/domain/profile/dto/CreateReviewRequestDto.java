package dutchiepay.backend.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateReviewRequestDto {
    @NotBlank(message = "주문번호를 입력해주세요.")
    private Long orderId;
    @NotBlank(message = "리뷰 내용을 입력해주세요.")
    private String content;
    @NotBlank(message = "평점을 입력해주세요.")
    private Integer rating;
    private String[] reviewImg;
}
