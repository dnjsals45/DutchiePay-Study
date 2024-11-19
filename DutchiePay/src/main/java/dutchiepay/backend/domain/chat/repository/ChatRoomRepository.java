package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
