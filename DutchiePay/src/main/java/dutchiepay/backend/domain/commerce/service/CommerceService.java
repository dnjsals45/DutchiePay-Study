package dutchiepay.backend.domain.commerce.service;

import dutchiepay.backend.domain.commerce.dto.PaymentInfoResponseDto;
import dutchiepay.backend.domain.commerce.exception.CommerceErrorCode;
import dutchiepay.backend.domain.commerce.exception.CommerceException;
import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.order.repository.AskRepository;
import dutchiepay.backend.domain.order.repository.LikesRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.entity.*;
import dutchiepay.backend.global.security.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommerceService {

    private final BuyRepository buyRepository;
    private final LikesRepository likesRepository;
    private final AskRepository askRepository;
    private final ProductRepository productRepository;

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

    /**
     * 상품의 문의 내역 목록을 조회
     * Pagination 구현
     * @param buyId 조회할 상품의 게시글 Id
     * @param pageable pageable 객체
     * @return BuyAskResponseDto 문의 내역 dto
     */
    public Page<Ask> getBuyAsks(Long buyId, Pageable pageable) {

        return askRepository.findByBuyAndDeletedAtIsNull(buyRepository.findById(buyId)
                .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT)), pageable);
    }

    /**
     * 공동구매 게시글의 상품 정보 반환
     * @param buyId 상품의 게시글 Id
     * @return PaymentInfoResponseDto 상품의 특정 정보만 담을 dto
     */
    public PaymentInfoResponseDto getPaymentInfo(Long buyId) {
        return PaymentInfoResponseDto.toDto(buyRepository.findById(buyId)
                        .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT)));
    }
}
