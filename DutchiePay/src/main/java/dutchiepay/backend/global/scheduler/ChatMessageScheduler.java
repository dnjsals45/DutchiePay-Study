package dutchiepay.backend.global.scheduler;

import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.domain.chat.repository.MessageJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageScheduler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageJdbcRepository messageJdbcRepository;

    private static final String CHAT_KEY_PREFIX = "chat:";
    private static final String MESSAGES_SUFFIX = ":messages:";

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void syncMessageToDB() {
        log.info("채팅 메시지 동기화 스케쥴링 시작");
        String pattern = CHAT_KEY_PREFIX + "*" + MESSAGES_SUFFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            syncMessageFromKey(key);
        }
        log.info("채팅 메시지 동기화 스케쥴링 종료");
    }

    private void syncMessageFromKey(String key) {
        Set<Object> messages = redisTemplate.opsForZSet().rangeByScore(key, 0, -1);

        if (messages == null || messages.isEmpty()) {
            return;
        }

        List<MessageResponse> messageResponseList = new ArrayList<>();
        for (Object obj : messages) {
            MessageResponse mr = (MessageResponse) obj;
            messageResponseList.add(mr);
        }

        messageJdbcRepository.syncMessage(messageResponseList);
    }
}
