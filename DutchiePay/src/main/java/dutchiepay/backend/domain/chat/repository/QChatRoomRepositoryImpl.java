package dutchiepay.backend.domain.chat.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.chat.dto.GetMessageListResponseDto;
import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.entity.QMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QChatRoomRepositoryImpl implements QChatRoomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    QMessage message = QMessage.message;

    @Override
    public GetMessageListResponseDto findChatRoomMessages(Long chatRoomId, String cursorDate, Long cursorMessageId, Long limit) {
        cursorMessageId = cursorMessageId == null ? Long.MAX_VALUE : cursorMessageId;

        List<Tuple> tuple = jpaQueryFactory
                .select(message.messageId,
                        message.content,
                        message.date,
                        message.time,
                        message.senderId,
                        message.type)
                .from(message)
                .where(message.chatroom.chatroomId.eq(chatRoomId))
                .where(message.date.loe(cursorDate).and(message.messageId.loe(cursorMessageId)))
                .orderBy(message.date.desc(), message.messageId.desc())
                .limit(limit + 1)
                .fetch();

        List<MessageResponse> result = new ArrayList<>();

        if (tuple.isEmpty()) {
            return GetMessageListResponseDto.builder()
                    .messages(result)
                    .cursor(null)
                    .build();
        }

        int count = 0;
        for (Tuple t : tuple) {
            if (count >= limit) break;

            String date = t.get(message.date);
            String formatDate = date.replace("년 ", "-").replace("월 ", "-").replace("일", "");

            MessageResponse dto = MessageResponse.builder()
                    .messageId(t.get(message.messageId))
                    .content(t.get(message.content))
                    .date(formatDate)
                    .time(t.get(message.time))
                    .senderId(t.get(message.senderId))
                    .type(t.get(message.type))
                    .build();

            result.add(dto);
            count++;
        }

        Collections.reverse(result);

        String nextCursor = null;

        if (tuple.size() > limit) {
            Tuple next = tuple.get(limit.intValue());
            nextCursor = next.get(message.date) + next.get(message.messageId);
        } else {
            Long countRemain = jpaQueryFactory
                    .select(message.count())
                    .from(message)
                    .where(message.chatroom.chatroomId.eq(chatRoomId))
                    .where(message.date.lt(cursorDate))
                    .fetchOne();

            if (countRemain != null && countRemain > 0) {
                LocalDate prevDate = LocalDate.parse(cursorDate, DateTimeFormatter.ofPattern("yyyyMMdd")).minusDays(1);
                nextCursor = prevDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + Long.MAX_VALUE;
            }
        }

        return GetMessageListResponseDto.builder()
                .messages(result)
                .cursor(nextCursor)
                .build();
    }
}
