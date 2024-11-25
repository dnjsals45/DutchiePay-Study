package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.commerce.repository.ScoreRepository;
import dutchiepay.backend.domain.order.exception.*;
import dutchiepay.backend.domain.order.repository.*;
import dutchiepay.backend.domain.profile.dto.*;
import dutchiepay.backend.domain.profile.exception.ProfileErrorCode;
import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
import dutchiepay.backend.domain.profile.repository.ProfileRepository;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final AskRepository askRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final BuyRepository buyRepository;
    private final ScoreRepository scoreRepository;

    public MyPageResponseDto myPage(User user) {
        return MyPageResponseDto.from(user);
    }

    public List<MyGoodsResponseDto> getMyGoods(User user, Long page, Long limit, String filter) {
        if (filter != null && !(filter.equals("pending") || filter.equals("shipped") || filter.equals("delivered"))) {
                throw new ProfileErrorException(ProfileErrorCode.INVALID_ORDER_STATUS);
            }

        return profileRepository.getMyGoods(user, filter, PageRequest.of(page.intValue() - 1, limit.intValue()));
    }


    public List<MyPostsResponseDto> getMyPosts(User user, String type, Long page, Long limit) {
        if (type.equals("post")) {
            return profileRepository.getMyPosts(user, PageRequest.of(page.intValue() - 1, limit.intValue()));
        } else if (type.equals("comment")) {
            return profileRepository.getMyCommentsPosts(user, PageRequest.of(page.intValue() - 1, limit.intValue()));
        } else {
            throw new ProfileErrorException(ProfileErrorCode.INVALID_POST_TYPE);
        }
    }


    public List<GetMyLikesResponseDto> getMyLike(User user) {
        return profileRepository.getMyLike(user);
    }

    public List<GetMyReviewResponseDto> getMyReviews(User user) {
        return reviewRepository.getMyReviews(user);
    }

    public GetMyReviewResponseDto getOneReview(User user, Long reviewId) {
        Review review = reviewRepository.findByUserAndReviewId(user, reviewId)
                .orElseThrow(() -> new ProfileErrorException(ProfileErrorCode.INVALID_REVIEW));

        return GetMyReviewResponseDto.from(review);
    }


    public List<GetMyAskResponseDto> getMyAsks(User user) {
        return askRepository.getMyAsks(user);
    }

    @Transactional
    public void createReview(User user, CreateReviewRequestDto req) {
        StringBuilder sb  = new StringBuilder();

        if (req.getRating() != 1 && req.getRating() != 2 && req.getRating() != 3 && req.getRating() != 4 && req.getRating() != 5) {
            throw new ReviewErrorException(ReviewErrorCode.INVALID_RATING);
        }

        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new ProfileErrorException(ProfileErrorCode.INVALID_USER_ORDER_REVIEW);
        }

        if (reviewRepository.existsByUserAndOrderAndDeletedAtIsNull(user, order)) {
            throw new ReviewErrorException(ReviewErrorCode.ALREADY_EXIST);
        }

        String reviewImg = null;
        if (req.getReviewImg() != null && req.getReviewImg().length > 0) {
            for (String img : req.getReviewImg()) {
                sb.append(img).append(",");
            }
            reviewImg = sb.substring(0, sb.length() - 1);
        }

        Review newReview = Review.builder()
                .user(user)
                .order(order)
                .contents(req.getContent())
                .rating(req.getRating())
                .reviewImg(reviewImg)
                .updateCount(0)
                .build();

        reviewRepository.save(newReview);

        Score score = scoreRepository.findByBuy(order.getBuy());
        if (score == null) {
            score = Score.builder()
                    .buy(order.getBuy())
                    .one(req.getRating() == 1 ? 1 : 0)
                    .two(req.getRating() == 2 ? 1 : 0)
                    .three(req.getRating() == 3 ? 1 : 0)
                    .four(req.getRating() == 4 ? 1 : 0)
                    .five(req.getRating() == 5 ? 1 : 0)
                    .count(1)
                    .build();

            scoreRepository.save(score);
        } else {
            score.addReview(req.getRating());
        }
    }

    @Transactional
    public void createAsk(User user, CreateAskRequestDto req) {
        Buy buy = buyRepository.findById(req.getBuyId()).orElseThrow(() -> new AskErrorException(AskErrorCode.INVALID_BUY));

        Ask newAsk = Ask.builder()
                .user(user)
                .buy(buy)
                .product(buy.getProduct())
                .contents(req.getContent())
                .secret(req.getIsSecret())
                .answer(null)
                .answeredAt(null)
                .build();

        askRepository.save(newAsk);
    }

    @Transactional
    public void changeNickname(User user, String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserErrorException(UserErrorCode.USER_NICKNAME_ALREADY_EXISTS);
        }
        user.changeNickname(nickname);

        userRepository.save(user);
    }

    @Transactional
    public void changeProfileImage(User user, String profileImg) {
        user.changeProfileImg(profileImg);

        userRepository.save(user);
    }

    @Transactional
    public void changeLocation(User user, String location) {
        user.changeLocation(location);

        userRepository.save(user);
    }

    @Transactional
    public void changePhone(User user, String phone) {
        user.changePhone(phone);

        userRepository.save(user);
    }

    @Transactional
    public void deleteAsk(User user, Long askId) {
        Ask ask = askRepository.findById(askId).orElseThrow(() -> new AskErrorException(AskErrorCode.INVALID_ASK));

        if (!ask.getUser().getUserId().equals(user.getUserId())) {
            throw new ProfileErrorException(ProfileErrorCode.DELETE_ASK_USER_MISSMATCH);
        }

        askRepository.softDelete(ask);
    }

    @Transactional
    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewErrorException(ReviewErrorCode.INVALID_REVIEW));

        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new ProfileErrorException(ProfileErrorCode.DELETE_REVIEW_USER_MISSMATCH);
        }

        reviewRepository.softDelete(review);
    }

    @Transactional
    public void updateReview(User user, UpdateReviewRequestDto req) {
        StringBuilder sb = new StringBuilder();

        Review review = reviewRepository.findById(req.getReviewId())
                .orElseThrow(() -> new ReviewErrorException(ReviewErrorCode.INVALID_REVIEW));

        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new ProfileErrorException(ProfileErrorCode.UPDATE_REVIEW_USER_MISSMATCH);
        }

        long dayBetween = ChronoUnit.DAYS.between(review.getCreatedAt().toLocalDate(), LocalDate.now());
        if (dayBetween > 30) {
            throw new ReviewErrorException(ReviewErrorCode.CANNOT_UPDATE_CAUSE_30DAYS);
        } else if (review.getUpdateCount() == 2) {
            throw new ReviewErrorException(ReviewErrorCode.CANNOT_UPDATE_CAUSE_COUNT);
        } else {
            String reviewImg = null;
            if (req.getReviewImg() != null) {
                for (String img : req.getReviewImg()) {
                    sb.append(img).append(",");
                }
                reviewImg = sb.substring(0, sb.length() - 1);
            }

            review.update(req.getContent(), req.getRating(), reviewImg);
        }
    }
}
