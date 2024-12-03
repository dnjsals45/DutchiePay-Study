package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.image.service.ImageService;
import dutchiepay.backend.domain.profile.dto.CreateReviewRequestDto;
import dutchiepay.backend.domain.profile.dto.GetMyReviewResponseDto;
import dutchiepay.backend.domain.profile.dto.UpdateReviewRequestDto;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Review;
import dutchiepay.backend.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewValidator reviewValidator;
    private final ImageService imageService;
    private final ScoreService scoreService;
    private final ReviewUtilService reviewUtilService;

    @Transactional
    public void createReview(User user, CreateReviewRequestDto req) {
        Order order = reviewValidator.validateReviewCreation(user, req);

        String reviewImg = imageService.processImages(req.getReviewImg());

        createReviewEntity(user, order, req, reviewImg);

        scoreService.updateScore(order, req.getRating());
    }

    @Transactional
    public void updateReview(User user, UpdateReviewRequestDto req) {
        Review review = reviewValidator.validateReviewUpdate(user, req);

        int oldRating = review.getRating();
        int newRating = req.getRating();

        String reviewImg = imageService.processImages(req.getReviewImg());

        review.update(req.getContent(), req.getRating(), reviewImg);

        scoreService.updateScoreOnUpdate(review, oldRating, newRating);
    }

    @Transactional
    public void deleteReview(User user, Long reviewId) {
        Review review = reviewValidator.validateReviewDeletion(user, reviewId);

        int deleteRating = review.getRating();

        reviewUtilService.softDelete(review);

        scoreService.updateScoreOnDelete(review, deleteRating);
    }

    public GetMyReviewResponseDto getOneReview(User user, Long reviewId) {
        Review review = reviewUtilService.findByUserAndReviewId(user, reviewId);

        return GetMyReviewResponseDto.from(review, review.getOrder().getOrderNum());
    }

    public List<GetMyReviewResponseDto> getMyReviews(User user) {
        return reviewUtilService.getMyReviews(user);
    }

    private void createReviewEntity(User user, Order order, CreateReviewRequestDto req, String reviewImg) {
        Review newReview = Review.builder()
                            .user(user)
                            .order(order)
                            .contents(req.getContent())
                            .rating(req.getRating())
                            .reviewImg(reviewImg)
                            .updateCount(0)
                            .build();

        reviewUtilService.save(newReview);
    }
}
