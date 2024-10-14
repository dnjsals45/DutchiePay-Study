package dutchiepay.backend.domain.commerce.service;

import dutchiepay.backend.domain.commerce.exception.CommerceErrorCode;
import dutchiepay.backend.domain.commerce.exception.CommerceException;
import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.repository.LikesRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.domain.order.service.OrdersService;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.entity.Buy;
import dutchiepay.backend.entity.Likes;
import dutchiepay.backend.entity.Product;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.security.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommerceService {

    private final BuyRepository buyRepository;
    private final LikesRepository likesRepository;

    /**
     * 상품 좋아요
     * @param userDetails 사용자
     * @param buyId 좋아요 할 상품
     */
    @Transactional
    public void likes(UserDetailsImpl userDetails, Long buyId) {
        Buy buy = buyRepository.findById(buyId)
                .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT));
        Likes likes = likesRepository.findByUserAndBuy(userDetails.getUser(), buy);
        if (likes == null) {
            likesRepository.save(Likes.builder().user(userDetails.getUser()).buy(buy).build());
        } else {
            likesRepository.delete(likes);
        }
    }
}
