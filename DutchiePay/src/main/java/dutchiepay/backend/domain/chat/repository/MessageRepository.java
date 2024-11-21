package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.unreadCount = m.unreadCount - 1 WHERE m.messageId > :lastMessageId AND m.chatroom.chatroomId = :chatRoomId")
    void discountUnreadMessageCount(Long lastMessageId, Long chatRoomId);
}
