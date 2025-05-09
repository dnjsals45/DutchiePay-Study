package dutchiepay.backend.domain.chat.service;

import dutchiepay.backend.domain.chat.dto.GetMessageListResponseDto;
import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
        String zsetKey = CHAT_KEY_PREFIX + chatRoomId + MESSAGES_SUFFIX + message.getDate().replaceAll("[^0-9]", "");
        String hashKey = "read_count:" + chatRoomId;
        String messageIdField = String.valueOf(message.getMessageId());

        redisTemplate.opsForZSet().add(zsetKey, MessageResponse.of(message), message.getMessageId());

        redisTemplate.opsForHash().put(hashKey, messageIdField, message.getUnreadCount());
    }

    public GetMessageListResponseDto getMessageFromMemory(Long chatRoomId, String cursorDate, Long cursorMessageId, Long limit) {
        List<MessageResponse> totalDataList = new ArrayList<>();
        String nextCursor;
        Long remainingLimit = limit;
        String currentDate = cursorDate;

        String hashKey = "read_count:" + chatRoomId;

        LocalDate currentLocalDate = LocalDate.now();
        LocalDate sevenDaysAgo = currentLocalDate.minusDays(7);

        while (remainingLimit > 0) {
            LocalDate targetDate = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            if (targetDate.isBefore(sevenDaysAgo)) {
                break;
            }

            String redisKey = "chat:" + chatRoomId + ":messages:" + currentDate;

            Set<Object> messages;
            if (cursorMessageId == null) {
                messages = redisTemplate.opsForZSet().reverseRange(redisKey, 0, remainingLimit);
            } else {
                messages = redisTemplate.opsForZSet().reverseRangeByScore(redisKey, 0, cursorMessageId, 0, remainingLimit + 1L);
            }

            if (messages == null || messages.isEmpty()) {
                currentDate = targetDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                cursorMessageId = null;
                continue;
            }

            int count = 0;
            for (Object obj : messages) {
                if (count < remainingLimit) {
                    MessageResponse mr = (MessageResponse) obj;

                    Integer rawUnread = (Integer) redisTemplate.opsForHash().get(hashKey, String.valueOf(mr.getMessageId()));
                    int unreadCount = rawUnread != null ? rawUnread : 0;

                    mr.setUnreadCount(unreadCount);

                    MessageResponse formatted = MessageResponse.builder()
                            .messageId(mr.getMessageId())
                            .content(mr.getContent())
                            .date(LocalDate.parse(mr.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd"))
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .time(mr.getTime())
                            .senderId(mr.getSenderId())
                            .type(mr.getType())
                            .unreadCount(unreadCount)
                            .build();

                    totalDataList.add(formatted);
                } else {
                    MessageResponse lastMessage = (MessageResponse) obj;
                    nextCursor = currentDate + lastMessage.getMessageId();

                    Collections.reverse(totalDataList);
                    return GetMessageListResponseDto.builder()
                            .messages(totalDataList)
                            .cursor(nextCursor)
                            .build();
                }
                count++;
            }

            remainingLimit -= messages.size();
            if (remainingLimit > 0) {
                currentDate = targetDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                cursorMessageId = null;
            }
        }

        String finalCursor = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd"))
                .minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "00";

        Collections.reverse(totalDataList);
        return GetMessageListResponseDto.builder()
                .messages(totalDataList)
                .cursor(finalCursor)
                .build();
    }

//    public void decreaseUnreadCountWithCursor(Long chatRoomId, Long cursorId) {
//        String pattern = "chat:" + chatRoomId + ":messages:*";
//        Set<String> keys = redisTemplate.keys(pattern);
//
//        if (keys == null || keys.isEmpty()) {
//            return;
//        }
//
//        for (String key : keys) {
//            Set<Object> messages = redisTemplate.opsForZSet().rangeByScore(key, cursorId, Double.MAX_VALUE);
//
//            if (messages != null) {
//                for (Object obj : messages) {
//                    MessageResponse mr = (MessageResponse) obj;
//                    if (mr.getMessageId() > cursorId && mr.getUnreadCount() > 0) {
//                        redisTemplate.opsForZSet().remove(key, mr);
//                        mr.setUnreadCount(mr.getUnreadCount() - 1);
//                        redisTemplate.opsForZSet().add(key, mr, mr.getMessageId());
//                    }
//                }
//            } else {
//                break;
//            }
//        }
//    }
    public void decreaseUnreadCountWithCursor(Long chatRoomId, Long cursorId) {
        String pattern = CHAT_KEY_PREFIX + chatRoomId + ":messages:*";
        Set<String> keys = redisTemplate.keys(pattern);
        String hashKey = "read_count:" + chatRoomId;

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Set<Object> messages = redisTemplate.opsForZSet().rangeByScore(key, cursorId + 1, Double.MAX_VALUE);

            if (messages == null || messages.isEmpty()) continue;

            for (Object obj : messages) {
                MessageResponse mr = (MessageResponse) obj;

                String messageId = String.valueOf(mr.getMessageId());

                Long updatedCount = redisTemplate.opsForHash().increment(hashKey, messageId, -1);

                if (updatedCount != null && updatedCount < 0) {
                    redisTemplate.opsForHash().put(hashKey, messageId, 0);
                }
            }
        }
    }
}
