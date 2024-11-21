package dutchiepay.backend.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.entity.QMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QMessageRepositoryImpl implements QMessageRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QMessage message = QMessage.message;

    @Override
    public Long findCursorId(Long chatRoomId) {
        return jpaQueryFactory
                .select(message.messageId)
                .from(message)
                .where(message.chatroom.chatroomId.eq(chatRoomId))
                .orderBy(message.messageId.asc())
                .limit(1)
                .fetchOne();
    }
}
