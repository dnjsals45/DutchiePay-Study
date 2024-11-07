package dutchiepay.backend.domain.profile.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateReviewRequestDto {
    private Long reviewId;
    private Integer rating;
    private String content;
    private String[] reviewImg;
}
