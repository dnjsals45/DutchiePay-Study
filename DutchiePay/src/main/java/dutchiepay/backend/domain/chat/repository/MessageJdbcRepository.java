package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.domain.chat.DateTimeUtil;
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
import java.sql.Timestamp;
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

    public void upsertMessages(List<Message> messages, Long chatRoomId) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO message (message_id, chatroom_id, sender_id, type, content, unread_count, date, time, created_at, updated_at, deleted_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, now(), NULL) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "unread_count = VALUES(unread_count)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Message msg = messages.get(i);
                        ps.setLong(1, msg.getMessageId());
                        ps.setLong(2, chatRoomId);
                        ps.setLong(3, msg.getSenderId());
                        ps.setString(4, msg.getType());
                        ps.setString(5, msg.getContent());
                        ps.setInt(6, msg.getUnreadCount());
                        ps.setString(7, msg.getDate());
                        ps.setString(8, msg.getTime());

                        LocalDateTime createdAt = DateTimeUtil.toLocalDateTime(msg.getDate(), msg.getTime());
                        ps.setTimestamp(9, Timestamp.valueOf(createdAt));
                    }

                    public int getBatchSize() {
                        return messages.size();
                    }
                }
        );
    }
}
