package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.order.exception.ReviewErrorCode;
import dutchiepay.backend.domain.order.exception.ReviewErrorException;
import dutchiepay.backend.domain.order.service.OrderUtilService;
import dutchiepay.backend.domain.profile.dto.CreateReviewRequestDto;
import dutchiepay.backend.domain.profile.dto.UpdateReviewRequestDto;
import dutchiepay.backend.domain.profile.exception.ProfileErrorCode;
import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Review;
import dutchiepay.backend.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class ReviewValidator {
    private final ReviewUtilService reviewUtilService;
    private final OrderUtilService orderUtilService;

    public Order validateReviewCreation(User user, CreateReviewRequestDto req) {
        validateReviewContentLength(req.getContent());
        validateRating(req.getRating());
        Order order = orderUtilService.findById(req.getOrderId());
        validateUserOrder(user, order);
        validateReviewDuplication(user, order);
        return order;
    }

    public void validateReviewContentLength(String content) {
        if (content.length() > 1000) {
            throw new ReviewErrorException(ReviewErrorCode.INVALID_CONTENT_LENGTH);
        }
    }

    public Review validateReviewUpdate(User user, UpdateReviewRequestDto req) {
        Review review = reviewUtilService.findById(req.getReviewId());

        validateUserReview(user, review);
        validateUpdateCondition(review);

        return review;
    }

    public Review validateReviewDeletion(User user, Long reviewId) {
        Review review = reviewUtilService.findById(reviewId);

        validateUserReview(user, review);
        return review;
    }

    private void validateUserReview(User user, Review review) {
        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new ReviewErrorException(ReviewErrorCode.REVIEW_USER_MISS_MATCH);
        }
    }

    private void validateReviewDuplication(User user, Order order) {
        if (reviewUtilService.existsByUserAndOrderAndDeletedAtIsNull(user, order)) {
            throw new ReviewErrorException(ReviewErrorCode.ALREADY_EXIST);
        }
    }

    private void validateUserOrder(User user, Order order) {
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new ProfileErrorException(ProfileErrorCode.INVALID_USER_ORDER_REVIEW);
        }
    }

    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new ReviewErrorException(ReviewErrorCode.INVALID_RATING);
        }
    }

    private void validateUpdateCondition(Review review) {
        long dayBetween = ChronoUnit.DAYS.between(review.getCreatedAt().toLocalDate(), LocalDate.now());

        if (dayBetween > 30) {
            throw new ReviewErrorException(ReviewErrorCode.CANNOT_UPDATE_CAUSE_30DAYS);
        }

        if (review.getUpdateCount() == 2) {
            throw new ReviewErrorException(ReviewErrorCode.CANNOT_UPDATE_CAUSE_COUNT);
        }
    }
}
