package dutchiepay.backend.domain.profile.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateReviewRequestDto {
    private Long orderId;
    private String content;
    private Long rating;
    private String reviewImg;
}
