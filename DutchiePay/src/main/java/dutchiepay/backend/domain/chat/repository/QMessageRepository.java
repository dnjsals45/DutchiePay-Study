package dutchiepay.backend.domain.chat.repository;

public interface QMessageRepository {
    Long findCursorId(Long chatRoomId);
}
