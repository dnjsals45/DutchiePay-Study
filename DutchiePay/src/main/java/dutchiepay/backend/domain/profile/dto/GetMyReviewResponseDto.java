package dutchiepay.backend.domain.profile.dto;

import dutchiepay.backend.entity.Review;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyReviewResponseDto {
    private Long reviewId;
    private Long buyId;
    private String productName;
    private Integer rating;
    private String content;
    private LocalDate createdAt;
    private Boolean isPossible;
    private String[] reviewImg;


    public static GetMyReviewResponseDto from(Review review) {
        LocalDate createdAt = review.getCreatedAt().toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(createdAt, LocalDate.now());

        return GetMyReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .buyId(review.getOrder().getBuy().getBuyId())
                .productName(review.getOrder().getProduct().getProductName())
                .rating(review.getRating())
                .content(review.getContents())
                .createdAt(createdAt)
                .isPossible(daysBetween <= 30 && review.getUpdateCount() != 3)
                .reviewImg(review.getReviewImg() != null ? review.getReviewImg().split(",") : new String[0])
                .build();
    }
}
