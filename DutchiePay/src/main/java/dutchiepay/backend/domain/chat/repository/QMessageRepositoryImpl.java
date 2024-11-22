package dutchiepay.backend.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.entity.QMessage;
import dutchiepay.backend.entity.QUserChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QMessageRepositoryImpl implements QMessageRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QMessage message = QMessage.message;
    QUserChatRoom userChatRoom = QUserChatRoom.userChatRoom;

    @Override
    public Long findCursorId(Long chatRoomId, Long userId) {
        return jpaQueryFactory
                .select(message.messageId)
                .from(message)
                .join(userChatRoom)
                .on(userChatRoom.chatroom.chatroomId.eq(chatRoomId)
                        .and(userChatRoom.user.userId.eq(userId)))
                .where(message.chatroom.chatroomId.eq(chatRoomId)
                        .and(message.messageId.gt(userChatRoom.lastMessageId)))
                .orderBy(message.messageId.asc())
                .limit(1)
                .fetchOne();
    }
}
