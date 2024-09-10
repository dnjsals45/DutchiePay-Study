package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.coupon.repository.UsersCouponRepository;
import dutchiepay.backend.domain.order.repository.*;
import dutchiepay.backend.domain.profile.dto.*;
import dutchiepay.backend.domain.profile.exception.ProfileErrorCode;
import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
import dutchiepay.backend.domain.profile.repository.ProfileRepository;
import dutchiepay.backend.entity.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UsersCouponRepository usersCouponRepository;
    private final OrdersRepository ordersRepository;
    private final ReviewRepository reviewRepository;
    private final AskRepository askRepository;
    private final ProfileRepository profileRepository;

    public MyPageResponseDto myPage(User user) {
        Long couponCount = usersCouponRepository.countByUser(user);

        // TODO 현재 참여한 진행중인 공구수 필요. 유저가 공구에 참여중이란 걸 어떻게 알 수 있을까?
        // 유저가 결제완료 + 아직 진행중인 공구일 경우
        Long orderCount = 0L;

        return MyPageResponseDto.from(user, couponCount, orderCount);
    }

    public List<MyGoodsResponseDto> getMyGoods(User user, Long page, Long limit) {
        return profileRepository.getMyGoods(user, PageRequest.of(page.intValue(), limit.intValue()));
    }


    public Object getMyPosts(User user, Long page, Long limit) {
        return null;
    }


    public List<GetMyLikesResponseDto> getMyLike(User user, String category) {
        return profileRepository.getMyLike(user, category);
    }

    public List<GetMyReviewResponseDto> getMyReviews(User user) {
        List<Review> reviews = reviewRepository.findAllByUser(user);

        return GetMyReviewResponseDto.from(reviews);
    }


    public List<GetMyAskResponseDto> getMyAsks(User user) {
        List<Ask> asks = askRepository.findAllByUser(user);

        return GetMyAskResponseDto.from(asks);
    }

    @Transactional
    public void createReview(User user, @Valid CreateReviewRequestDto req) {
        Orders order = ordersRepository.findById(req.getOrderId()).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

        Review newReview = Review.builder()
                .user(order.getUser())
                .buy(order.getBuy())
                .contents(req.getContent())
                .rating(req.getRating())
                .build();

        reviewRepository.save(newReview);
    }

    @Transactional
    public void createAsk(User user, @Valid CreateAskRequestDto req) {
        Orders order = ordersRepository.findById(req.getOrderId()).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

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
        user.changeNickname(nickname);
    }

    @Transactional
    public void changeProfileImage(User user, String profileImg) {
        user.changeProfileImg(profileImg);
    }

    @Transactional
    public void changeLocation(User user, String location) {
        user.changeLocation(location);
    }

    @Transactional
    public void changeAddress(User user, ChangeAddressRequestDto req) {
        user.changeAddress(req.getAddress(), req.getDetail());
    }

    @Transactional
    public void changePhone(User user, String phone) {
        user.changePhone(phone);
    }

    @Transactional
    public void deleteAsk(User user, Long askId) {
        Ask ask = askRepository.findById(askId).orElseThrow(() -> new ProfileErrorException(ProfileErrorCode.INVALID_ASK));

        if (ask.getUser() != user) {
            throw new ProfileErrorException(ProfileErrorCode.DELETE_USER_MISSMATCH);
        }

        askRepository.softDelete(ask);
    }
}
