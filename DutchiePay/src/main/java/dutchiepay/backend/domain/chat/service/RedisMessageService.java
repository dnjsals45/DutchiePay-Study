package dutchiepay.backend.domain.chat.service;

import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis key 형식: chat:{chatRoomId}:messages:yyMMdd
    private static final String CHAT_KEY_PREFIX = "chat:";
    private static final String MESSAGES_SUFFIX = ":messages:";

    public void saveMessage(String chatRoomId, Message message) {
        String redisKey = CHAT_KEY_PREFIX + chatRoomId + MESSAGES_SUFFIX + message.getDate().replaceAll("[^0-9]", "");
        redisTemplate.opsForZSet().add(redisKey, MessageResponse.of(message), message.getMessageId());
    }

    public void getMessageFromMemory(Long chatRoomId, int page) {
        String redisKey = CHAT_KEY_PREFIX + chatRoomId + MESSAGES_SUFFIX + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        int pageSize = 10;
        int start = (page - 1) * pageSize;
        int end = start + pageSize - 1;

        Set<Object> objects = redisTemplate.opsForZSet().range(redisKey, start, end);
        for (Object obj : objects) {
            MessageResponse mr = (MessageResponse) obj;
            log.info("mr: {}", mr.getContent());
        }
    }
}
