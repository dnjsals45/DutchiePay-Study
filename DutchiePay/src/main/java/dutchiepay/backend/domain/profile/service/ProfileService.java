package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.order.exception.OrderErrorCode;
import dutchiepay.backend.domain.order.exception.OrderErrorException;
import dutchiepay.backend.domain.order.exception.ReviewErrorCode;
import dutchiepay.backend.domain.order.exception.ReviewErrorException;
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
import jakarta.validation.Valid;
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

    public MyPageResponseDto myPage(User user) {
        return MyPageResponseDto.from(user);
    }

    public List<MyGoodsResponseDto> getMyGoods(User user, Long page, Long limit) {
        return profileRepository.getMyGoods(user, PageRequest.of(page.intValue() - 1, limit.intValue()));
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
        List<Review> reviews = reviewRepository.findAllByUser(user);

        return GetMyReviewResponseDto.from(reviews);
    }

    public GetMyReviewResponseDto getOneReview(User user, Long reviewId) {
        Review review = reviewRepository.findByUserAndReviewId(user, reviewId)
                .orElseThrow(() -> new ProfileErrorException(ProfileErrorCode.INVALID_REVIEW));

        return GetMyReviewResponseDto.from(review);
    }


    public List<GetMyAskResponseDto> getMyAsks(User user) {
        List<Ask> asks = askRepository.findAllByUser(user);

        return GetMyAskResponseDto.from(asks);
    }

    @Transactional
    public void createReview(User user, CreateReviewRequestDto req) {
        StringBuilder sb  = new StringBuilder();

        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new OrderErrorException(OrderErrorCode.INVALID_ORDER));

        if (user != order.getUser()) {
            throw new ProfileErrorException(ProfileErrorCode.INVALID_USER_ORDER_REVIEW);
        }

        if (reviewRepository.existsByUserAndOrder(user, order)) {
            throw new ReviewErrorException(ReviewErrorCode.ALREADY_EXIST);
        }

        String reviewImg = null;
        if (req.getReviewImg() != null) {
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
    }

    @Transactional
    public void createAsk(User user, CreateAskRequestDto req) {
        Order order = orderRepository.findById(req.getOrderId()).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

        if (user != order.getUser()) {
            throw new ProfileErrorException(ProfileErrorCode.INVALID_USER_ORDER_REVIEW);
        }

        Ask newAsk = Ask.builder()
                .user(user)
                .buy(order.getBuy())
                .product(order.getProduct())
                .orderNum(order.getOrderNum())
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
        Ask ask = askRepository.findById(askId).orElseThrow(() -> new ProfileErrorException(ProfileErrorCode.INVALID_ASK));

        if (ask.getUser() != user) {
            throw new ProfileErrorException(ProfileErrorCode.DELETE_ASK_USER_MISSMATCH);
        }

        askRepository.softDelete(ask);
    }

    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewErrorException(ReviewErrorCode.INVALID_REVIEW));

        if (review.getUser() != user) {
            throw new ProfileErrorException(ProfileErrorCode.DELETE_REVIEW_USER_MISSMATCH);
        }

        reviewRepository.softDelete(review);
    }


    public void updateReview(User user, UpdateReviewRequestDto req) {
        StringBuilder sb = new StringBuilder();

        Review review = reviewRepository.findById(req.getReviewId())
                .orElseThrow(() -> new ReviewErrorException(ReviewErrorCode.INVALID_REVIEW));

        if (review.getUser() != user) {
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
