package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Buy;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BuyJdbcRepositoryImpl {
    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    public void bulkInsert(List<Buy> buys) {
        String sql = """
            INSERT INTO buy (product_id, title, deadline, skeleton, now_count, tags, created_at, updated_at, deleted_at)
            VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), NULL)
        """;

        for (int start = 0; start < buys.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, buys.size());
            List<Buy> batchList = buys.subList(start, end);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Buy buy = batchList.get(i);
                    ps.setLong(1, buy.getProduct().getProductId());
                    ps.setString(2, buy.getTitle());
                    ps.setObject(3, buy.getDeadline());
                    ps.setInt(4, buy.getSkeleton());
                    ps.setInt(5, buy.getNowCount());
                    ps.setString(6, buy.getTags());
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });
        }
    }
}