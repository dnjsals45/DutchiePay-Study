package dutchiepay.backend.domain.chat.service;

import dutchiepay.backend.domain.chat.dto.GetMessageListResponseDto;
import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.domain.chat.exception.ChatErrorCode;
import dutchiepay.backend.domain.chat.exception.ChatException;
import dutchiepay.backend.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public GetMessageListResponseDto getMessageFromMemory(Long chatRoomId, String cursorDate, Long cursorMessageId, Long limit) {
        String redisKey = CHAT_KEY_PREFIX + chatRoomId + MESSAGES_SUFFIX + cursorDate;

        Set<Object> messages;
        if (cursorMessageId == null) {
            messages = redisTemplate.opsForZSet()
                    .reverseRange(redisKey, 0, limit);
        } else {
            messages = redisTemplate.opsForZSet()
                    .reverseRangeByScore(redisKey,
                            0,  // 최소값
                            cursorMessageId,  // Long 타입 그대로 사용
                            0,
                            limit + 1L);
        }

        if (messages == null || messages.isEmpty()) {
            return null;
        }

        List<MessageResponse> dataList = new ArrayList<>();
        String nextCursor = null;

        int count = 0;

        for (Object obj : messages) {
            if (count < limit) {
                MessageResponse mr = (MessageResponse) obj;
                dataList.add(mr);
            } else {
                MessageResponse lastMessage = (MessageResponse) obj;
                nextCursor = cursorDate + lastMessage.getMessageId();
                break;
            }
            count++;
        }

        return GetMessageListResponseDto.builder()
                .messages(dataList)
                .cursor(nextCursor)
                .build();
    }
}
