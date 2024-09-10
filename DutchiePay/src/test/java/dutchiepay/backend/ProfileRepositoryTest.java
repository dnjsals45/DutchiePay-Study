package dutchiepay.backend;

import dutchiepay.backend.domain.commerce.BuyCategory;
import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.commerce.repository.ScoreRepository;
import dutchiepay.backend.domain.commerce.repository.StoreRepository;
import dutchiepay.backend.domain.order.repository.LikesRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.domain.profile.dto.GetMyLikesResponseDto;
import dutchiepay.backend.domain.profile.repository.ProfileRepository;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
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

    private List<GetMyLikesResponseDto> result = new ArrayList<>();

    private User user;

    private Buy buy;

    private Product product;

    private Store store;

    private Likes like;

    private Score score;

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
}
