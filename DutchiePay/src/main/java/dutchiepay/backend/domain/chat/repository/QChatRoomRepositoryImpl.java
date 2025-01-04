package dutchiepay.backend.domain.chat.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.chat.dto.GetMessageListResponseDto;
import dutchiepay.backend.entity.QMessage;
import dutchiepay.backend.entity.QUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QChatRoomRepositoryImpl implements QChatRoomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    QMessage message = QMessage.message;
    QUser user = QUser.user;


    @Override
    public List<GetMessageListResponseDto> findChatRoomMessages(Long chatRoomId) {
        List<Tuple> tuple = jpaQueryFactory
                .select(message.messageId,
                        message.content,
                        message.date,
                        message.time,
                        message.senderId,
                        user.nickname,
                        user.profileImg,
                        message.type)
                .from(message)
                .leftJoin(user).on(user.userId.eq(message.senderId))
                .where(message.chatroom.chatroomId.eq(chatRoomId))
                .orderBy(message.date.asc(), message.time.asc())
                .fetch();

        List<GetMessageListResponseDto> result = new ArrayList<>();

        for (Tuple t : tuple) {
            String date = t.get(message.date);
            String formatDate = date.replace("년 ", "-")
                    .replace("월 ", "-")
                    .replace("일", "");

            GetMessageListResponseDto dto = GetMessageListResponseDto.builder()
                    .messageId(t.get(message.messageId))
                    .content(t.get(message.content))
                    .date(formatDate)
                    .sendAt(t.get(message.time))
                    .senderId(t.get(message.senderId))
                    .senderName(t.get(user.nickname))
                    .senderProfileImg(t.get(user.profileImg))
                    .type(t.get(message.type))
                    .build();

            result.add(dto);
        }

        return result;
    }
}
