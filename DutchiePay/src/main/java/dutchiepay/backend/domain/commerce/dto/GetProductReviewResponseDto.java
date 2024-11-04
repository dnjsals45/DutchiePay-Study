package dutchiepay.backend.domain.commerce.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetProductReviewResponseDto {
    private Double avg;
    private Long total;
    private List<ReviewDto> reviews;

    public static GetProductReviewResponseDto from(Double avg, Long total, List<ReviewDto> reviews) {
        return GetProductReviewResponseDto.builder()
                .avg(avg)
                .total(total)
                .reviews(reviews)
                .build();
    }

    @Getter
    @Builder
    public static class ReviewDto {
        private Long reviewId;
        private String nickname;
        private String content;
        private Integer rating;
        private String[] reviewImg;
        private LocalDateTime createdAt;
    }
}
