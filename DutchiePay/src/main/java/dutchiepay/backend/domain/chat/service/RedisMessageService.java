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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis key 형식: chat:{chatRoomId}:messages:yyyyMMdd
    private static final String CHAT_KEY_PREFIX = "chat:";
    private static final String MESSAGES_SUFFIX = ":messages:";

    public void saveMessage(String chatRoomId, Message message) {
        String redisKey = CHAT_KEY_PREFIX + chatRoomId + MESSAGES_SUFFIX + message.getDate().replaceAll("[^0-9]", "");
        redisTemplate.opsForZSet().add(redisKey, MessageResponse.of(message), message.getMessageId());
    }

    public GetMessageListResponseDto
    getMessageFromMemory(Long chatRoomId, String cursorDate, Long cursorMessageId, Long limit) {
        List<MessageResponse> totalDataList = new ArrayList<>();
        String nextCursor;
        Long remainingLimit = limit;
        String currentDate = cursorDate;

        LocalDate currentLocalDate = LocalDate.now();
        LocalDate sevenDaysAgo = currentLocalDate.minusDays(7);

        while (remainingLimit > 0) {
            LocalDate targetDate = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            if (targetDate.isBefore(sevenDaysAgo)) {
                break;
            }

            String redisKey = CHAT_KEY_PREFIX + chatRoomId + MESSAGES_SUFFIX + currentDate;

            Set<Object> messages;
            if (cursorMessageId == null) {
                messages = redisTemplate.opsForZSet()
                        .reverseRange(redisKey, 0, remainingLimit);
            } else {
                messages = redisTemplate.opsForZSet()
                        .reverseRangeByScore(redisKey,
                                0,
                                cursorMessageId,
                                0,
                                remainingLimit + 1L);
            }

            if (messages == null || messages.isEmpty()) {
                LocalDate date = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalDate previousDate = date.minusDays(1);
                currentDate = previousDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                cursorMessageId = null;
                continue;
            }

            int count = 0;
            for (Object obj : messages) {
                if (count < remainingLimit) {
                    MessageResponse mr = (MessageResponse) obj;
                    MessageResponse mr2 = MessageResponse.builder()
                            .messageId(mr.getMessageId())
                            .content(mr.getContent())
                            .date(LocalDate.parse(mr.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd"))
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .time(mr.getTime())
                            .senderId(mr.getSenderId())
                            .type(mr.getType())
                            .build();

                    totalDataList.add(mr2);
                } else {
                    MessageResponse lastMessage = (MessageResponse) obj;
                    nextCursor = currentDate + lastMessage.getMessageId();
                    return GetMessageListResponseDto.builder()
                            .messages(totalDataList)
                            .cursor(nextCursor)
                            .build();
                }
                count++;
            }

            remainingLimit -= messages.size();
            if (remainingLimit > 0) {
                LocalDate date = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalDate previousDate = date.minusDays(1);
                currentDate = previousDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                cursorMessageId = null;
            }
        }

        LocalDate date = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate previousDate = date.minusDays(1);

        return GetMessageListResponseDto.builder()
                .messages(totalDataList)
                .cursor(previousDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "00")
                .build();
    }
}
