package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.domain.chat.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void syncMessage(List<MessageResponse> messageResponseList) {
        String sql = """
            INSERT INTO message (message_id, sender_id, type, content, unread_count, date, time)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            sender_id = VALUES(sender_id),
            type = VALUES(type),
            content = VALUES(content),
            unread_count = VALUES(unread_count),
            date = VALUES(date),
            time = VALUES(time)
        """;

        int size = messageResponseList.size();
        jdbcTemplate.batchUpdate(sql, messageResponseList, size, (ps, message) -> {
            ps.setLong(1, message.getMessageId());
            ps.setLong(2, message.getSenderId());
            ps.setString(3, message.getType());
            ps.setString(4, message.getContent());
            ps.setInt(5, message.getUnreadCount());
            ps.setString(6, message.getDate());
            ps.setString(7, message.getTime());
        });
    }
}
