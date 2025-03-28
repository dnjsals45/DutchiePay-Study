package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.entity.ChatRoom;
import dutchiepay.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>, QMessageRepository {

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.unreadCount = m.unreadCount - 1 WHERE m.messageId > :lastMessageId AND m.chatroom.chatroomId = :chatRoomId")
    void discountUnreadMessageCount(Long lastMessageId, Long chatRoomId);

    List<Message> findAllByChatroom(ChatRoom chatRoom);

    List<Message> findAllByChatroomChatroomIdAndDate(Long chatRoomNumber, String date);
}
