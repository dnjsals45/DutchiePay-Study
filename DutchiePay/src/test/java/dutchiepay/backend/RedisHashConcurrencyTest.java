package dutchiepay.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class RedisHashConcurrencyTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String key = "read_count:1";
    private final String field = "3";

    @BeforeEach
    void setUp() {
        redisTemplate.delete(key);
        redisTemplate.opsForHash().put(key, field, 2);
    }

    @Test
    void testConcurrentHashHINCRBY() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            try {
                redisTemplate.opsForHash().increment(key, field, -1);
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(task);
        }

        latch.await();

        Object result = redisTemplate.opsForHash().get(key, field);
        System.out.println("최종 unreadCount: " + result);
        assertThat(result).isEqualTo(0);
    }
}
