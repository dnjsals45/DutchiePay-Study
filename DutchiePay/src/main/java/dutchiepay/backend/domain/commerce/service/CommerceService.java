package dutchiepay.backend.domain.commerce.service;

import dutchiepay.backend.domain.commerce.dto.*;
import dutchiepay.backend.domain.commerce.exception.CommerceErrorCode;
import dutchiepay.backend.domain.commerce.exception.CommerceException;
import dutchiepay.backend.domain.commerce.repository.*;
import dutchiepay.backend.domain.order.repository.AskRepository;
import dutchiepay.backend.domain.order.repository.LikeRepository;
import dutchiepay.backend.domain.order.repository.ProductJdbcRepositoryImpl;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.entity.*;
import dutchiepay.backend.global.security.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommerceService {

    private final BuyRepository buyRepository;
    private final LikeRepository likeRepository;
    private final AskRepository askRepository;
    private final ProductRepository productRepository;
    private final StoreJdbcRepositoryImpl storeJdbcRepository;
    private final ProductJdbcRepositoryImpl productJdbcRepository;
    private final BuyJdbcRepositoryImpl buyJdbcRepositoryImpl;

    /**
     * 상품 좋아요
     * @param userDetails 사용자
     * @param buyId 좋아요 할 상품
     */
    @Transactional
    public void likes(UserDetailsImpl userDetails, Long buyId) {
        Buy buy = buyRepository.findById(buyId)
                .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT));
        Like like = likeRepository.findByUserAndBuy(userDetails.getUser(), buy);
        if (like == null) {
            likeRepository.save(Like.builder().user(userDetails.getUser()).buy(buy).build());
        } else {
            likeRepository.delete(like);
        }
    }

    /**
     * 상품의 문의 내역 목록을 조회
     * Pagination 구현
     * @param buyId 조회할 상품의 게시글 Id
     * @param page 페이지
     * @param limit 한 페이지 개수
     * @return BuyAskResponseDto 문의 내역 dto
     */
    public List<BuyAskResponseDto> getBuyAsks(Long buyId, int page, int limit) {

        Buy buy = buyRepository.findById(buyId)
                .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT));

        return askRepository.findByBuyAndDeletedAtIsNull(buy, PageRequest.of(page - 1, limit))
                .stream().map(BuyAskResponseDto::toDto).collect(Collectors.toList());
    }


    public GetBuyResponseDto getBuyPage(User user, Long buyId) {
        if (!buyRepository.existsById(buyId)) {
            throw new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT);
        }

        return buyRepository.getBuyPageByBuyId(user, buyId);
    }

    public GetBuyListResponseDto getBuyList(User user, String filter, String category, int end, String cursor, int limit) {
        return buyRepository.getBuyList(user, filter, category, null, end, cursor, limit);
    }

    public List<GetProductReviewResponseDto> getProductReview(Long buyId, Long photo, Long page, Long limit) {
        if (!buyRepository.existsById(buyId)) {
            throw new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT);
        }
        return buyRepository.getProductReview(buyId, photo, PageRequest.of(page.intValue() - 1, limit.intValue()));
    }

    /**
     * 공동구매 게시글의 상품 정보 반환
     * @param buyId 상품의 게시글 Id
     * @return PaymentInfoResponseDto 상품의 특정 정보만 담을 dto
     */
    public PaymentInfoResponseDto getPaymentInfo(Long buyId) {
        Buy buy = buyRepository.findById(buyId)
                .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT));

        return PaymentInfoResponseDto.toDto(buy, productRepository.findById(buy.getProduct().getProductId())
                .orElseThrow(() -> new CommerceException(CommerceErrorCode.CANNOT_FOUND_PRODUCT)).getStore().getStoreName());
    }

    @Transactional
    public void addEntity(int size) {
        List<Store> stores = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            stores.add(Store.builder()
                    .storeName("Store_" + i)
                    .contactNumber("01000000000")
                    .representative("CEO_" + i)
                    .storeAddress("Address_" + i)
                    .build());
        }

        List<Store> storeList = storeJdbcRepository.bulkInsert(stores);

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            products.add(Product.builder()
                    .store(storeList.get(i))
                    .productName("Product " + i)
                    .detailImg("detail" + i + ".jpg")
                    .originalPrice(10000)
                    .salePrice(9000)
                    .discountPercent(10)
                    .productImg("product" + i + ".jpg")
                    .build());
        }

        List<Product> productList = productJdbcRepository.bulkInsert(products);

        List<Buy> buys = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            buys.add(Buy.builder()
                    .product(productList.get(i))
                    .title("테스트 공구" + i)
                    .deadline(LocalDate.now().plusDays(7))
                    .skeleton(50)
                    .nowCount(0)
                    .tags("생활용품")
                    .build());
        }

        buyJdbcRepositoryImpl.bulkInsert(buys);
    }
}
