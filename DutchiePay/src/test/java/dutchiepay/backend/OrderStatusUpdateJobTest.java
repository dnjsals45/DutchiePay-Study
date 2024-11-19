package dutchiepay.backend;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.commerce.repository.StoreRepository;
import dutchiepay.backend.domain.order.repository.OrderRepository;
import dutchiepay.backend.domain.order.repository.ProductRepository;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.*;
import dutchiepay.backend.global.scheduler.OrderStatusUpdateSchedule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderStatusUpdateJobTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BuyRepository buyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private OrderStatusUpdateSchedule schedule;

    @Test
    void 주문상태업데이트테스트() throws InterruptedException {
        //given
        User user = User.builder()
                .email("test@example.com")
                .username("테스트유저")
                .phone("01012345678")
                .nickname("테스트닉네임")
                .location("테스트지역")
                .state(0)
                .build();

        userRepository.save(user);

        Store store = Store.builder()
                .storeName("테스트매장")
                .contactNumber("01012345678")
                .representative("테스트대표")
                .storeAddress("테스트매장주소")
                .build();

        storeRepository.save(store);

        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product product = Product.builder()
                    .store(store)
                    .productName("테스트상품" + i)
                    .detailImg("테스트상품상세내용" + i)
                    .originalPrice(10000)
                    .salePrice(8000)
                    .discountPercent(20)
                    .productImg("테스트상품이미지" + i)
                    .build();

            productList.add(product);
        }

        productRepository.saveAll(productList);

        List<Buy> buyList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Buy notMeetBuy = Buy.builder()
                    .product(productList.get(i))
                    .title("충족상품" + i)
                    .deadline(LocalDate.now().minusDays(2))
                    .skeleton(10)
                    .nowCount(10)
                    .build();

            buyList.add(notMeetBuy);
        }

        buyRepository.saveAll(buyList);

        List<Order> orderList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Order order1 = Order.builder()
                    .user(user)
                    .product(productList.get(i))
                    .buy(buyList.get(i))
                    .receiver("테스트수령인")
                    .zipCode("12345")
                    .phone("01012345678")
                    .address("테스트주소")
                    .quantity(1)
                    .totalPrice(8000)
                    .payment("kakao")
                    .orderedAt(LocalDateTime.now().minusDays(3))
                    .statusChangeDate(LocalDate.now().minusDays(3))
                    .state("공구진행중")
                    .orderNum("ABCD1234")
                    .build();

            orderList.add(order1);
        }

        orderRepository.saveAll(orderList);
        // when
        CronTrigger cronTrigger = new CronTrigger("0 10 0 * * ?");
        cronTrigger.nextExecution(new SimpleTriggerContext());

        taskScheduler.schedule(schedule::orderStatusUpdate, new Date());

        Thread.sleep(1000);

        // then
        for (int i = 0; i < 10; i++) {
            assertThat(orderRepository.findById(orderList.get(i).getOrderId()).get().getState()).isEqualTo("배송준비중");
        }
    }
}
