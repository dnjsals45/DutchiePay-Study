package dutchiepay.backend.domain.chat.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.ChronoUtil;
import dutchiepay.backend.domain.chat.dto.GetChatRoomListResponseDto;
import dutchiepay.backend.domain.chat.dto.GetChatRoomUsersResponseDto;
import dutchiepay.backend.entity.QChatRoom;
import dutchiepay.backend.entity.QMessage;
import dutchiepay.backend.entity.QUserChatRoom;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QUserChatRoomRepositoryImpl implements QUserChatRoomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    QUserChatRoom userChatRoom = QUserChatRoom.userChatRoom;
    QMessage message = QMessage.message;
    QChatRoom chatRoom = QChatRoom.chatRoom;

    @Override
    public List<GetChatRoomListResponseDto> getChatRoomList(User user) {
        List<Tuple> queryResult = jpaQueryFactory
                .select(chatRoom.chatroomId,
                        chatRoom.chatRoomName,
                        chatRoom.chatRoomImg,
                        JPAExpressions
                                .select(userChatRoom.user.count())
                                .from(userChatRoom)
                                .where(userChatRoom.chatroom.chatroomId.eq(chatRoom.chatroomId)),
                        JPAExpressions
                                .select(message.count())
                                .from(message)
                                .where(message.chatroom.chatroomId.eq(chatRoom.chatroomId)
                                        .and(message.messageId.gt(
                                                JPAExpressions
                                                        .select(userChatRoom.lastMessageId)
                                                        .from(userChatRoom)
                                                        .where(userChatRoom.chatroom.chatroomId.eq(chatRoom.chatroomId)
                                                                .and(userChatRoom.user.eq(user)))
                                        ))),
                        message.content,
                        message.date,
                        message.time,
                        message.type,
                        chatRoom.type)
                .from(chatRoom)
                .join(userChatRoom).on(userChatRoom.chatroom.eq(chatRoom)
                        .and(userChatRoom.user.eq(user)))
                .leftJoin(message).on(message.chatroom.eq(chatRoom)
                        .and(message.messageId.eq(
                                JPAExpressions
                                        .select(message.messageId.max())
                                        .from(message)
                                        .where(message.chatroom.eq(chatRoom))
                        )))
                .fetch();

        List<GetChatRoomListResponseDto> result = new ArrayList<>();

        for (Tuple tuple : queryResult) {
            GetChatRoomListResponseDto dto = GetChatRoomListResponseDto.builder()
                    .chatRoomId(tuple.get(chatRoom.chatroomId))
                    .chatName(tuple.get(chatRoom.chatRoomName))
                    .chatImg(tuple.get(chatRoom.chatRoomImg))
                    .chatUser(tuple.get(3, Long.class).intValue())
                    .unreadCount(tuple.get(4, Long.class).intValue())
                    .lastMsg("img".equals(tuple.get(message.type)) ? "이미지를 전송했습니다." : tuple.get(message.content))
                    .lastChatTime(ChronoUtil.formatChatTime(tuple.get(message.date), tuple.get(message.time)))
                    .type(tuple.get(chatRoom.type))
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    public List<GetChatRoomUsersResponseDto> getChatRoomUsers(Long chatRoomId) {
        List<Tuple> tuple = jpaQueryFactory
                .select(userChatRoom.user.userId,
                        userChatRoom.user.nickname,
                        userChatRoom.user.profileImg,
                        userChatRoom.role)
                .from(userChatRoom)
                .where(userChatRoom.chatroom.chatroomId.eq(chatRoomId)
                        .and(userChatRoom.banned.eq(false)))
                .fetch();

        List<GetChatRoomUsersResponseDto> result = new ArrayList<>();

        for (Tuple t : tuple) {
            GetChatRoomUsersResponseDto dto = GetChatRoomUsersResponseDto.builder()
                    .userId(t.get(userChatRoom.user.userId))
                    .nickname(t.get(userChatRoom.user.nickname))
                    .profileImg(t.get(userChatRoom.user.profileImg))
                    .isManager(t.get(userChatRoom.role).equals("manager") ? true : false)
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    public Boolean findByUserBanned(Long userId, Long chatRoomId) {
        return jpaQueryFactory
                .select(userChatRoom.banned)
                .from(userChatRoom)
                .where(userChatRoom.user.userId.eq(userId)
                        .and(userChatRoom.chatroom.chatroomId.eq(chatRoomId)))
                .fetchOne();
    }

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

    @Override
    public void updateLastMessageToUser(Long userId, Long chatRoomId) {
        Long latestMessageId = jpaQueryFactory
                .select(message.messageId)
                .from(message)
                .where(message.chatroom.chatroomId.eq(chatRoomId))
                .orderBy(message.messageId.desc())
                .limit(1)
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
