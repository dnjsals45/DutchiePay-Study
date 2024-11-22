package dutchiepay.backend.domain.chat.repository;

import org.springframework.transaction.annotation.Transactional;

public interface QUserChatRoomRepository {
    @Transactional
    void updateLastMessageLatestMessageId(long userId, Long chatRoomId);

    @Transactional
    void updateLastMessageToUser(Long userId, Long chatRoomId);
}
