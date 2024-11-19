package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
