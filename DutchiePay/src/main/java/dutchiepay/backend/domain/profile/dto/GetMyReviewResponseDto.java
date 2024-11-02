package dutchiepay.backend.domain.profile.dto;

import dutchiepay.backend.entity.Review;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyReviewResponseDto {
    private Long reviewId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isPossible;
    private String reviewImg;


    public static GetMyReviewResponseDto from(Review review) {
        return GetMyReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating())
                .content(review.getContents())
                .createdAt(review.getCreatedAt())
                .isPossible(review.getUpdateCount() != 2)
                .reviewImg(review.getReviewImg())
                .build();
    }

    public static List<GetMyReviewResponseDto> from(List<Review> reviews) {
        List<GetMyReviewResponseDto> response = new ArrayList<>();

        for (Review review : reviews) {
            response.add(GetMyReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .rating(review.getRating())
                    .content(review.getContents())
                    .createdAt(review.getCreatedAt())
                    .isPossible(true)
                    .reviewImg(review.getReviewImg())
                    .build());
        }

        return response;
    }
}
