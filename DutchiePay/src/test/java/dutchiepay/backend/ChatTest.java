package dutchiepay.backend;

import dutchiepay.backend.domain.chat.dto.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class ChatTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String key = "chat:1:messages:20250423";

    @BeforeEach
    void setUp() {
        redisTemplate.delete(key);
        MessageResponse message = MessageResponse.builder()
                .messageId(3L)
                .unreadCount(2)
                .content("Hello")
                .build();

        redisTemplate.opsForZSet().add(key, message, message.getMessageId());
    }

    @Test
    void testConcurrentZSetModification() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            try {
                Set<Object> result = redisTemplate.opsForZSet().rangeByScore(key, 3, 3);
                for (Object obj : result) {
                    MessageResponse mr = (MessageResponse) obj;

                    redisTemplate.opsForZSet().remove(key, mr);

                    Thread.sleep(50);

                    mr.setUnreadCount(mr.getUnreadCount() - 1);

                    redisTemplate.opsForZSet().add(key, mr, mr.getMessageId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(task);
        }

        latch.await();

        Set<Object> finalResult = redisTemplate.opsForZSet().rangeByScore(key, 3, 3);
        for (Object obj : finalResult) {
            MessageResponse mr = (MessageResponse) obj;
            System.out.println("최종 unreadCount: " + mr.getUnreadCount());
            assertThat(mr.getUnreadCount()).isEqualTo(0);
        }
    }
}
