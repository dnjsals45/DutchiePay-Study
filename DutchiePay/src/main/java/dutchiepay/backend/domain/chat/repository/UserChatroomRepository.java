package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatroomRepository extends JpaRepository<UserChatRoom, Long> {
}
