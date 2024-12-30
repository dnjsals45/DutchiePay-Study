package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.order.exception.ReviewErrorCode;
import dutchiepay.backend.domain.order.exception.ReviewErrorException;
import dutchiepay.backend.domain.order.repository.ReviewRepository;
import dutchiepay.backend.domain.profile.dto.GetMyReviewResponseDto;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Review;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewUtilService {
    private final ReviewRepository reviewRepository;

    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewErrorException(ReviewErrorCode.INVALID_REVIEW));
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public Review findByUserAndReviewId(User user, Long reviewId) {
        return reviewRepository.findByUserAndReviewId(user, reviewId)
                .orElseThrow(() -> new ReviewErrorException(ReviewErrorCode.INVALID_REVIEW));
    }

    public void softDelete(Review review) {
        reviewRepository.softDelete(review);
    }

    public boolean existsByUserAndOrderAndDeletedAtIsNull(User user, Order order) {
        return reviewRepository.existsByUserAndOrderAndDeletedAtIsNull(user, order);
    }

    public List<GetMyReviewResponseDto> getMyReviews(User user) {
        return reviewRepository.getMyReviews(user);
    }
}
