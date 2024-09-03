package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.coupon.repository.UsersCouponRepository;
import dutchiepay.backend.domain.profile.dto.*;
import dutchiepay.backend.domain.user.service.UserUtilService;
import dutchiepay.backend.entity.User;
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
        return null;
    }

    public Object getMyReviews(Long userId) {
        User user = userUtilService.findById(userId);

        return null;
    }


    public Object getMyAsks(Long userId) {
        User user = userUtilService.findById(userId);
        return null;
    }

    public Object createReview(Long userId, @Valid CreateReviewRequestDto req) {
        userUtilService.findById(userId);

        // TODO orderId 검증 필요

        return null;
    }

    public Object createAsk(Long userId, @Valid CreateAskRequestDto req) {
        return null;
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
}
