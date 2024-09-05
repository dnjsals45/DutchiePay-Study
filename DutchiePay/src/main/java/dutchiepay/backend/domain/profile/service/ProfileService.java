package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.coupon.repository.UsersCouponRepository;
import dutchiepay.backend.domain.order.repository.*;
import dutchiepay.backend.domain.profile.dto.*;
import dutchiepay.backend.domain.profile.exception.ProfileErrorCode;
import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
import dutchiepay.backend.domain.user.service.UserUtilService;
import dutchiepay.backend.entity.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserUtilService userUtilService;
    private final UsersCouponRepository usersCouponRepository;
    private final OrdersRepository ordersRepository;
    private final ReviewRepository reviewRepository;
    private final AskRepository askRepository;
    private final LikesRepository likesRepository;

    public MyPageResponseDto myPage(Long userId) {
        User user = userUtilService.findById(userId);

        Long couponCount = usersCouponRepository.countByUser(user);

        // TODO 현재 참여한 진행중인 공구수 필요. 유저가 공구에 참여중이란 걸 어떻게 알 수 있을까?
        Long orderCount = 0L;

        return MyPageResponseDto.from(user, couponCount, orderCount);
    }

    public List<MyGoodsResponseDto> getMyGoods(Long userId, Long page, Long limit) {
        User user = userUtilService.findById(userId);

        return null;
    }


    public Object getMyPosts(Long userId, Long page, Long limit) {
        User user = userUtilService.findById(userId);
        return null;
    }


    public Object getMyLike(Long userId, String category) {
        User user = userUtilService.findById(userId);

        List<Likes> likes = likesRepository.findAllByUser(user);


        return null;
    }

    public List<GetMyReviewResponseDto> getMyReviews(Long userId) {
        User user = userUtilService.findById(userId);

        List<Review> reviews = reviewRepository.findAllByUser(user);

        return GetMyReviewResponseDto.from(reviews);
    }


    public List<GetMyAskResponseDto> getMyAsks(Long userId) {
        User user = userUtilService.findById(userId);

        List<Ask> asks = askRepository.findAllByUser(user);

        return GetMyAskResponseDto.from(asks);
    }

    @Transactional
    public void createReview(Long userId, @Valid CreateReviewRequestDto req) {
        userUtilService.findById(userId);

        Orders order = ordersRepository.findById(req.getOrderId()).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

        Review newReview = Review.builder()
                .user(order.getUser())
                .buyPost(order.getBuyPost())
                .contents(req.getContent())
                .rating(req.getRating())
                .build();

        reviewRepository.save(newReview);
    }

    @Transactional
    public void createAsk(Long userId, @Valid CreateAskRequestDto req) {
        User user = userUtilService.findById(userId);

        Orders order = ordersRepository.findById(req.getOrderId()).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

        Ask newAsk = Ask.builder()
                .user(user)
                .buyPost(order.getBuyPost())
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
    public void changeNickname(Long userId, String nickname) {
        User user = userUtilService.findById(userId);

        user.changeNickname(nickname);
    }

    @Transactional
    public void changeProfileImage(Long userId, String profileImg) {
        User user = userUtilService.findById(userId);

        user.changeProfileImg(profileImg);
    }

    @Transactional
    public void changeLocation(Long userId, String location) {
        User user = userUtilService.findById(userId);

        user.changeLocation(location);
    }

    @Transactional
    public void changeAddress(Long userId, ChangeAddressRequestDto req) {
        User user = userUtilService.findById(userId);

        user.changeAddress(req.getAddress(), req.getDetail());
    }

    @Transactional
    public void changePhone(Long userId, String phone) {
        User user = userUtilService.findById(userId);

        user.changePhone(phone);
    }

    @Transactional
    public void deleteAsk(Long userId, Long askId) {
        User user = userUtilService.findById(userId);

        Ask ask = askRepository.findById(askId).orElseThrow(() -> new IllegalArgumentException("문의 정보가 없습니다."));

        if (ask.getUser() != user) {
            throw new ProfileErrorException(ProfileErrorCode.DELETE_USER_MISSMATCH);
        }

        askRepository.softDelete(ask);
    }
}
