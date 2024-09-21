package dutchiepay.backend;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.coupon.repository.CouponRepository;
import dutchiepay.backend.domain.coupon.repository.UsersCouponRepository;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.Coupon;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.Users_Coupon;
import dutchiepay.backend.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
@Import(UsersCouponRepositoryPerformanceTest.TestConfig.class)
class UsersCouponRepositoryPerformanceTest {

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
    private UsersCouponRepository usersCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    private Coupon coupon;

    private List<User> users;

    @BeforeEach
    public void setUp() {
        coupon = Coupon.builder()
                .couponName("테스트 쿠폰")
                .expireDate(LocalDate.of(2022, 12, 31))
                .percentage(10)
                .requirePrice(10000)
                .build();

        couponRepository.save(coupon);
    }

    @AfterEach
    public void resetPrepareData() {
        users.clear();
    }

    private User prepareUser(String nickname) {
        User user = User.builder()
                .email("test@example.com")
                .username("테스트")
                .phone("01012345678")
                .nickname(nickname)
                .location("서울시 어딘가")
                .state(0)
                .build();

        return userRepository.save(user);
    }

    private void prepare(int userNum, int couponNum) {
        users = new ArrayList<>();
        for (int i = 0; i < userNum; i++) {
            String nickname = "테스트 유저" + i;
            User user = prepareUser(nickname);
            users.add(user);
        }

        for (User user : users) {
            for (int i = 0; i < couponNum; i++) {
                Users_Coupon usersCoupon = Users_Coupon.builder()
                        .user(user)
                        .coupon(coupon)
                        .build();

                usersCouponRepository.save(usersCoupon);
            }
        }
    }

    @Test
    @Disabled
    void 성능테스트_유저1_쿠폰100() {
        prepare(1, 100);
        User testUser1 = users.get(0);
        System.out.println("testUser1 = " + testUser1.getNickname());

        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUser(testUser1);
        }
        long durationCountByUser = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUserUserId(testUser1.getUserId());
        }
        long durationCountByUserId = System.nanoTime() - startTime;

        System.out.println("countByUser duration (ns): " + durationCountByUser / 100);
        System.out.println("countByUserUserId duration (ns): " + durationCountByUserId / 100);

        assertEquals(usersCouponRepository.countByUser(testUser1), usersCouponRepository.countByUserUserId(testUser1.getUserId()));
    }

    @Test
    @Disabled
    void 성능테스트_유저1_쿠폰1000() {
        prepare(1, 1000);
        User testUser1 = users.get(0);
        System.out.println("testUser1 = " + testUser1.getNickname());

        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUser(testUser1);
        }
        long durationCountByUser = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUserUserId(testUser1.getUserId());
        }
        long durationCountByUserId = System.nanoTime() - startTime;

        System.out.println("countByUser duration (ns): " + durationCountByUser / 100);
        System.out.println("countByUserUserId duration (ns): " + durationCountByUserId / 100);

        assertEquals(usersCouponRepository.countByUser(testUser1), usersCouponRepository.countByUserUserId(testUser1.getUserId()));
    }

    @Test
    @Disabled
    void 성능테스트_유저1_쿠폰10000() {
        prepare(1, 10000);
        User testUser1 = users.get(0);
        System.out.println("testUser1 = " + testUser1.getNickname());

        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUser(testUser1);
        }
        long durationCountByUser = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUserUserId(testUser1.getUserId());
        }
        long durationCountByUserId = System.nanoTime() - startTime;

        System.out.println("countByUser duration (ns): " + durationCountByUser / 100);
        System.out.println("countByUserUserId duration (ns): " + durationCountByUserId / 100);

        assertEquals(usersCouponRepository.countByUser(testUser1), usersCouponRepository.countByUserUserId(testUser1.getUserId()));
    }

    @Test
    @Disabled
    void 성능테스트_유저10_쿠폰100() {
        prepare(10, 100);
        User testUser1 = users.get(0);
        System.out.println("testUser1 = " + testUser1.getNickname());

        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUser(testUser1);
        }
        long durationCountByUser = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUserUserId(testUser1.getUserId());
        }
        long durationCountByUserId = System.nanoTime() - startTime;

        System.out.println("countByUser duration (ns): " + durationCountByUser / 100);
        System.out.println("countByUserUserId duration (ns): " + durationCountByUserId / 100);

        assertEquals(usersCouponRepository.countByUser(testUser1), usersCouponRepository.countByUserUserId(testUser1.getUserId()));
    }

    @Test
    @Disabled
    void 성능테스트_유저10_쿠폰1000() {
        prepare(10, 1000);
        User testUser1 = users.get(0);
        System.out.println("testUser1 = " + testUser1.getNickname());

        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUser(testUser1);
        }
        long durationCountByUser = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUserUserId(testUser1.getUserId());
        }
        long durationCountByUserId = System.nanoTime() - startTime;

        System.out.println("countByUser duration (ns): " + durationCountByUser / 100);
        System.out.println("countByUserUserId duration (ns): " + durationCountByUserId / 100);

        assertEquals(usersCouponRepository.countByUser(testUser1), usersCouponRepository.countByUserUserId(testUser1.getUserId()));
    }

    @Test
    @Disabled
    void 성능테스트_유저10_쿠폰10000() {
        prepare(10, 10000);
        User testUser1 = users.get(0);
        System.out.println("testUser1 = " + testUser1.getNickname());

        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUser(testUser1);
        }
        long durationCountByUser = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            usersCouponRepository.countByUserUserId(testUser1.getUserId());
        }
        long durationCountByUserId = System.nanoTime() - startTime;

        System.out.println("countByUser duration (ns): " + durationCountByUser / 100);
        System.out.println("countByUserUserId duration (ns): " + durationCountByUserId / 100);

        assertEquals(usersCouponRepository.countByUser(testUser1), usersCouponRepository.countByUserUserId(testUser1.getUserId()));
    }
}
