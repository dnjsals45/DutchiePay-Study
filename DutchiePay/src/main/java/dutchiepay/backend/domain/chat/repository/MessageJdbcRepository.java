package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    public void batchInsert(List<Message> messages) {
        String sql = """
        INSERT INTO message (chatroom_id, type, sender_id, content, date, time, unread_count, created_at, updated_at, deleted_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NULL)
    """;

        LocalDateTime now = LocalDateTime.now();

        for (int start = 0; start < messages.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, messages.size());
            List<Message> batchList = messages.subList(start, end);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Message msg = batchList.get(i);
                    ps.setLong(1, msg.getChatroom().getChatroomId());
                    ps.setString(2, msg.getType());
                    ps.setLong(3, msg.getSenderId());
                    ps.setString(4, msg.getContent());
                    ps.setString(5, msg.getDate());
                    ps.setString(6, msg.getTime());
                    ps.setInt(7, msg.getUnreadCount());
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });
        }
    }

    @Transactional
    public void syncMessage(List<MessageResponse> messageResponseList, Long chatRoomId) {
        String sql = """
            INSERT INTO message (
                message_id, chatroom_id, sender_id, type, content, unread_count, 
                date, time, created_at, updated_at, deleted_at
            )
            VALUES (
                ?, ?, ?, ?, ?, ?,
                ?, ?, NOW(), NOW(), NULL
            )
            ON DUPLICATE KEY UPDATE
                sender_id = VALUES(sender_id),
                chatroom_id = VALUES(chatroom_id),
                type = VALUES(type),
                content = VALUES(content),
                unread_count = VALUES(unread_count),
                date = VALUES(date),
                time = VALUES(time),
                updated_at = NOW()
        """;

        int size = messageResponseList.size();
        jdbcTemplate.batchUpdate(sql, messageResponseList, size, (ps, message) -> {
            ps.setLong(1, message.getMessageId());
            ps.setLong(2, chatRoomId);
            ps.setLong(3, message.getSenderId());
            ps.setString(4, message.getType());
            ps.setString(5, message.getContent());
            ps.setInt(6, message.getUnreadCount());
            ps.setString(7, message.getDate());
            ps.setString(8, message.getTime());
        });
    }
}
