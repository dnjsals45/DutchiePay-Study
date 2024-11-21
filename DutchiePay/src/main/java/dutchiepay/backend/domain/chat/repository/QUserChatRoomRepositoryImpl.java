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
public class QUserChatRoomRepositoryImpl implements QUserChatRoomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    QUserChatRoom userChatRoom = QUserChatRoom.userChatRoom;
    QMessage message = QMessage.message;

    @Override
    public void updateLastMessageLatestMessageId(long userId, Long chatRoomId) {
        Long latestMessageId = jpaQueryFactory
                .select(message.messageId.max())
                .from(message)
                .where(message.chatroom.chatroomId.eq(chatRoomId))
                .fetchOne();

        if (latestMessageId != null) {
            jpaQueryFactory
                    .update(userChatRoom)
                    .set(userChatRoom.lastMessageId, latestMessageId)
                    .where(userChatRoom.user.userId.eq(userId)
                            .and(userChatRoom.chatroom.chatroomId.eq(chatRoomId)))
                    .execute();
        }
    }
}
