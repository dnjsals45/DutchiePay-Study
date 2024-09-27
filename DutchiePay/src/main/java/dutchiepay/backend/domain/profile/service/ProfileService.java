package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.commerce.BuyCategory;
import dutchiepay.backend.domain.coupon.repository.UsersCouponRepository;
import dutchiepay.backend.domain.order.repository.*;
import dutchiepay.backend.domain.profile.dto.*;
import dutchiepay.backend.domain.profile.exception.ProfileErrorCode;
import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
import dutchiepay.backend.domain.profile.repository.AddressRepository;
import dutchiepay.backend.domain.profile.repository.ProfileRepository;
import dutchiepay.backend.domain.profile.repository.UsersAddressRepository;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UsersAddressRepository usersAddressRepository;

    public MyPageResponseDto myPage(User user) {
        List<Address> addressList = addressRepository.findAllByUser(user);
        Long couponCount = usersCouponRepository.countByUser(user);
        Long orderCount = ordersRepository.countByUserPurchase(user, "결제 완료");

        return MyPageResponseDto.from(user, addressList, couponCount, orderCount);
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


    public List<GetMyLikesResponseDto> getMyLike(User user, String category) {
        if (!BuyCategory.isExist(category)) {
            throw new ProfileErrorException(ProfileErrorCode.INVALID_CATEGORY);
        }

        return profileRepository.getMyLike(user, category);
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
        Orders order = ordersRepository.findById(req.getOrderId()).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

        if (user != order.getUser()) {
            throw new ProfileErrorException(ProfileErrorCode.INVALID_USER_ORDER_REVIEW);
        }

        Review newReview = Review.builder()
                .user(user)
                .buy(order.getBuy())
                .contents(req.getContent())
                .rating(req.getRating())
                .build();

        reviewRepository.save(newReview);
    }

    @Transactional
    public void createAsk(User user, CreateAskRequestDto req) {
        Orders order = ordersRepository.findById(req.getOrderId()).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

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
    public void changeAddress(User user, ChangeAddressRequestDto req) {
        Address address = addressRepository.findById(req.getAddressId())
                        .orElseThrow(() -> new ProfileErrorException(ProfileErrorCode.INVALID_ADDRESS));

        address.update(req);
        addressRepository.save(address);
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
            throw new ProfileErrorException(ProfileErrorCode.DELETE_USER_MISSMATCH);
        }

        askRepository.softDelete(ask);
    }

    @Transactional
    public void addAddress(User user, @Valid CreateAddressRequestDto req) {
        Address newAddress = Address.builder()
                .addressName(req.getAddressName())
                .receiver(req.getName())
                .phone(req.getPhone())
                .addressInfo(req.getAddress())
                .detail(req.getDetail())
                .zipCode(req.getZipCode())
                .isDefault(req.getIsDefault())
                .build();

        if (newAddress.getIsDefault().equals(Boolean.TRUE)) {
            addressRepository.changeIsDefaultTrueToFalse(user);
        }

        addressRepository.save(newAddress);

        UsersAddress usersAddress = UsersAddress.builder()
                .user(user)
                .address(newAddress)
                .build();

        usersAddressRepository.save(usersAddress);
    }

    @Transactional
    public void deleteAddress(User user, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ProfileErrorException(ProfileErrorCode.INVALID_ADDRESS));

        if (address.getIsDefault().equals(Boolean.TRUE)) {
            addressRepository.changeOldestAddressToDefault(user);
        }

        usersAddressRepository.deleteByUserAndAddress(user, address);
        addressRepository.delete(address);
    }
}
