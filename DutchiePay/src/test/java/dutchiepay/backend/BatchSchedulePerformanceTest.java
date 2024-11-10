package dutchiepay.backend;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.commerce.repository.StoreRepository;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
@Import(BatchSchedulePerformanceTest.TestConfig.class)
class BatchSchedulePerformanceTest {

    @TestConfiguration
    static class TestConfig {
        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BuyRepository buyRepository;

    private void prepareData(int userNum, int buyNum, int orderNum) {
        List<User> users = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < userNum; i++) {
            User user = User.builder()
                    .email("test" + i + "@example.com")
                    .username("테스트" + i)
                    .phone("01012345678")
                    .nickname("테스트 유저" + i)
                    .location("서울시")
                    .state(0)
                    .build();

            users.add(user);
            userRepository.save(user);
        }

        for (int i = 0; i < buyNum; i++) {
            Store store = Store.builder()
                    .storeName("매장" + i)
                    .contactNumber("01012345678")
                    .representative("대표" + i)
                    .storeAddress("서울시")
                    .build();

            storeRepository.save(store);

            Product product = Product.builder()
                    .store(store)
                    .productName("테스트상품" + i)
                    .detailImg("상세이미지")
                    .originalPrice(10000)
                    .salePrice(8000)
                    .discountPercent(20)
                    .productImg("상품이미지")
                    .build();

            productRepository.save(product);

            Buy buy = Buy.builder()
                    .product(product)
                    .title("테스트공구" + i)
                    .deadline(LocalDate.now())
                    .skeleton(10)
                    .nowCount(0)
                    .build();

            buyRepository.save(buy);

            for (User user : users) {
                for (int j = 0; j < orderNum; j++) {
                    Order order = Order.builder()
                            .user(user)
                            .product(product)
                            .buy(buy)
                            .receiver("수령인")
                            .phone("01012345678")
                            .zipCode("12345")
                            .address("서울시")
                            .totalPrice(8000)
                            .payment("kakao")
                            .orderedAt(LocalDateTime.now())
                            .orderNum("주문번호" + i + "_" + j)
                            .state("공구진행중")
                            .quantity(1)
                            .build();

                    orderRepository.save(order);
                    orders.add(order);
                }
            }
        }

    }

    @Test
    void 단순업데이트테스트_유저1_공구1_주문1() {
        prepareData(1, 1, 1);

        long startTime = System.nanoTime();
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            order.confirmPurchase();
        }

        orderRepository.saveAll(orders);
        long duration = System.nanoTime() - startTime;

        System.out.println("durationTime = " + duration);
    }


}
