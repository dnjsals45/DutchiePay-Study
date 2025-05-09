package dutchiepay.backend.global.scheduler;

import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.domain.chat.repository.MessageJdbcRepository;
import dutchiepay.backend.entity.ChatRoom;
import dutchiepay.backend.entity.Message;
import dutchiepay.backend.global.scheduler.service.AsyncMessageTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageScheduler {
    private final AsyncMessageTaskService asyncMessageTaskService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CHAT_KEY_PREFIX = "chat:";
    private static final String MESSAGES_SUFFIX = ":messages:";
    private static final int RETENTION_DAYS = 6;

    @Scheduled(cron = "0 0 3 * * ?")
    @SchedulerLock(name = "syncMessageToDB", lockAtMostFor = "PT5M", lockAtLeastFor = "PT1M")
    public void syncAllMessagesToDB() {
        String pattern = "chat:*:messages:*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("동기화할 메시지가 없습니다.");
            return;
        }

        for (String key : keys) {
            try {
                asyncMessageTaskService.syncMessageFromKey(key);
            } catch (Exception e) {
                log.error("키 [{}] 동기화 중 오류 발생", key, e);
            }
        }
    }

    @Scheduled(cron = "0 0 4 * * ?")
    @SchedulerLock(name = "cleanupOldMessages", lockAtMostFor = "PT5M", lockAtLeastFor = "PT1M")
    public void cleanupOldMessages() {
        log.info("Redis 메시지 정리 스케줄링 시작");
        String pattern = CHAT_KEY_PREFIX + "*" + MESSAGES_SUFFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("삭제할 메시지가 없습니다.");
            return;
        }

        LocalDate cutoffDate = LocalDate.now().minusDays(RETENTION_DAYS);

        for (String key : keys) {
            try {
                deleteIfOldMessageAndHash(key, cutoffDate);
            } catch (Exception e) {
                log.warn("삭제 도중 오류 발생 - key: {}", key, e);
            }
        }
        log.info("🧹 Redis 메시지 정리 스케줄링 종료");
    }

    private void deleteIfOldMessageAndHash(String key, LocalDate cutoffDate) {
        String dateStr = key.substring(key.lastIndexOf(":") + 1);
        LocalDate messageDate = LocalDate.parse(dateStr, DateTimeFormatter.BASIC_ISO_DATE);

        if (messageDate.isBefore(cutoffDate)) {
            redisTemplate.delete(key);
            log.info("삭제된 메시지 키: {}", key);

            String chatRoomId = key.split(":")[1];
            String hashKey = "read_count:" + chatRoomId;
            redisTemplate.delete(hashKey);
            log.info("삭제된 읽음 해시 키: {}", hashKey);
        }
    }
}
