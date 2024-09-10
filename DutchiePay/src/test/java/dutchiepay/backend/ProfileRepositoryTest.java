package dutchiepay.backend;

import dutchiepay.backend.domain.commerce.BuyCategory;
import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.commerce.repository.ScoreRepository;
import dutchiepay.backend.domain.commerce.repository.StoreRepository;
import dutchiepay.backend.domain.order.repository.LikesRepository;
import dutchiepay.backend.domain.order.repository.OrdersRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.domain.profile.dto.GetMyLikesResponseDto;
import dutchiepay.backend.domain.profile.dto.MyGoodsResponseDto;
import dutchiepay.backend.domain.profile.repository.ProfileRepository;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("prod")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuyRepository buyRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    private User user;

    private Buy buy;

    private Product product;

    private Store store;

    private Likes like;

    private Score score;

    private Orders orders;


    @BeforeEach
    public void setUp() {
    }

    @Test
    @Transactional
    void 좋아요누른상품찾기() {
        // given
        // 유저
        user = User.builder()
                .email("test@example.com")
                .username("test")
                .nickname("더취3")
                .location("서울시 마포구")
                .phone("01012345678")
                .state(0)
                .build();

        userRepository.save(user);

        // 업체
        store = Store.builder()
                .storeName("테스트 업체")
                .contactNumber("024343434")
                .representative("테스트 대표")
                .storeAddress("서울시 마포구")
                .build();
        storeRepository.save(store);

        // 상품
        product = Product.builder()
                .storeId(store)
                .productName("테스트 상품")
                .detailImg("테스트 상세 이미지")
                .originalPrice(10000)
                .salePrice(9000)
                .discountPercent(10)
                .productImg("테스트 상품 이미지")
                .build();
        productRepository.save(product);

        // 공구게시글
        buy = Buy.builder()
                .productId(product)
                .title("테스트 공구 게시글1")
                .deadline(LocalDate.now())
                .skeleton(10)
                .nowCount(100)
                .category(BuyCategory.가전)
                .build();
        buyRepository.save(buy);

        like = Likes.builder()
                .user(user)
                .buy(buy)
                .build();
        likesRepository.save(like);

        score = Score.builder()
                .buy(buy)
                .one(1)
                .two(2)
                .three(3)
                .four(4)
                .five(5)
                .count(15)
                .build();
        scoreRepository.save(score);


        // when
        List<GetMyLikesResponseDto> result = new ArrayList<>();

        result = profileRepository.getMyLike(user, "디지털/가전");
        
        // then
        GetMyLikesResponseDto like = result.get(0);
        System.out.println("like.getCategory() = " + like.getCategory());
        System.out.println("like.getTitle() = " + like.getTitle());
        System.out.println("like.getOriginalPrice() = " + like.getOriginalPrice());
        System.out.println("like.getSalePrice() = " + like.getSalePrice());
        System.out.println("like.getDiscountPercent() = " + like.getDiscountPercent());
        System.out.println("like.getThumbnail() = " + like.getThumbnail());
        System.out.println("like.getAverage() = " + like.getAverage());
        System.out.println("like.getReviewCount() = " + like.getReviewCount());
        System.out.println("like.getExpireDate() = " + like.getExpireDate());
    }

    @Test
    @Transactional
    void 구매내역조회() {
        // given
        // 유저
        user = User.builder()
                .email("test@example.com")
                .username("test")
                .nickname("더취3")
                .location("서울시 마포구")
                .phone("01012345678")
                .state(0)
                .build();

        userRepository.save(user);

        // 업체
        store = Store.builder()
                .storeName("테스트 업체")
                .contactNumber("024343434")
                .representative("테스트 대표")
                .storeAddress("서울시 마포구")
                .build();
        storeRepository.save(store);

        // 상품
        Product product1 = Product.builder()
                .storeId(store)
                .productName("테스트 상품")
                .detailImg("테스트 상세 이미지")
                .originalPrice(10000)
                .salePrice(9000)
                .discountPercent(10)
                .productImg("테스트 상품 이미지")
                .build();

        Product product2 = Product.builder()
                .storeId(store)
                .productName("테스트 상품2")
                .detailImg("테스트 상세 이미지2")
                .originalPrice(5000)
                .salePrice(4500)
                .discountPercent(10)
                .productImg("테스트 상품 이미지2")
                .build();
        productRepository.save(product1);
        productRepository.save(product2);

        // 주문
        Orders orders1 = Orders.builder()
                .user(user)
                .product(product1)
                .buy(buy)
                .orderNum("2021090001")
                .amount(1)
                .totalPrice(18000)
                .payment("카드결제")
                .orderedAt(LocalDateTime.now())
                .address("연세대학교 1층 로비")
                .state("0")
                .amount(2)
                .build();

        Orders orders2 = Orders.builder()
                .user(user)
                .product(product2)
                .buy(buy)
                .orderNum("2021090002")
                .amount(1)
                .totalPrice(22500)
                .payment("카드결제")
                .orderedAt(LocalDateTime.now())
                .address("지나가던 버스 정류장 앞")
                .state("0")
                .amount(5)
                .build();
        ordersRepository.save(orders1);
        ordersRepository.save(orders2);

        // when
        List<MyGoodsResponseDto> result = profileRepository.getMyGoods(user, PageRequest.of(0, 1));

        // then
        for (MyGoodsResponseDto dto : result) {
            System.out.println("dto.getOrderId() = " + dto.getOrderId());
            System.out.println("dto.getOrderNum() = " + dto.getOrderNum());
            System.out.println("dto.getProductId() = " + dto.getProductId());
            System.out.println("dto.getOrderDate() = " + dto.getOrderDate());
            System.out.println("dto.getProductName() = " + dto.getProductName());
            System.out.println("dto.getCount() = " + dto.getCount());
            System.out.println("dto.getProductPrice() = " + dto.getProductPrice());
            System.out.println("dto.getTotalPrice() = " + dto.getTotalPrice());
            System.out.println("dto.getDiscountPercent() = " + dto.getDiscountPercent());
            System.out.println("dto.getPayment() = " + dto.getPayment());
            System.out.println("dto.getDeliveryAddress() = " + dto.getDeliveryAddress());
            System.out.println("dto.getDeliveryState() = " + dto.getDeliveryState());
            System.out.println("dto.getProductImg() = " + dto.getProductImg());
            System.out.println("dto.getStoreName() = " + dto.getStoreName());
            System.out.println("=============================================================");
        }

    }
}
