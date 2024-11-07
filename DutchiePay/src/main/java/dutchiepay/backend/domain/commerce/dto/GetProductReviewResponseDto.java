package dutchiepay.backend.domain.commerce.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetProductReviewResponseDto {
    private Long reviewId;
    private String nickname;
    private String content;
    private Integer rating;
    private String[] reviewImg;
    private LocalDate createdAt;
}
