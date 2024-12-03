package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final AskRepository askRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final BuyRepository buyRepository;

    public MyPageResponseDto myPage(User user) {
        return MyPageResponseDto.from(user);
    }

    public MyGoodsResponseDto getMyGoods(User user, Long page, Long limit, String filter) {
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

    public List<GetMyAskResponseDto> getMyAsks(User user) {
        return askRepository.getMyAsks(user);
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
}
