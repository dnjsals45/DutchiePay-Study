package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserChatroomRepository extends JpaRepository<UserChatRoom, Long>, QUserChatRoomRepository {

    @Modifying
    @Query("UPDATE UserChatRoom ucr SET ucr.lastMessageId = :messageId WHERE ucr.user.userId IN :userIds AND ucr.chatroom.chatroomId = :chatRoomId")
    void updateLastMessageToAllSubscribers(List<Long> userIds, long chatRoomId, long messageId);

    @Query("SELECT ucr.lastMessageId FROM UserChatRoom ucr WHERE ucr.user.userId = :userId AND ucr.chatroom.chatroomId = :chatRoomId")
    Long findLastMessageId(long userId, Long chatRoomId);

    List<UserChatRoom> findAllByUser(User user);

    @Query("SELECT ucr FROM UserChatRoom ucr WHERE ucr.user.userId = :userId")
    List<UserChatRoom> findAllByUserId(Long userId);
}
