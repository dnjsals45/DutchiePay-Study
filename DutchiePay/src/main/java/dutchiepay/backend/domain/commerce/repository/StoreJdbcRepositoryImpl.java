package dutchiepay.backend.domain.commerce.repository;

import dutchiepay.backend.entity.Store;
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
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreJdbcRepositoryImpl {
    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    @Transactional
    public List<Store> bulkInsert(List<Store> storeList) {
        String sql = """
        INSERT INTO store (
            store_name, contact_number, representative, store_address, created_at, updated_at, deleted_at
        ) VALUES (?, ?, ?, ?, NOW(), NOW(), NULL)
    """;

        int batchSize = 1000;  // BATCH_SIZE 설정
        List<Store> insertedStores = new ArrayList<>();

        for (int start = 0; start < storeList.size(); start += batchSize) {
            int end = Math.min(start + batchSize, storeList.size());
            List<Store> batchList = storeList.subList(start, end);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Store store = batchList.get(i);
                    ps.setString(1, store.getStoreName());
                    ps.setString(2, store.getContactNumber());
                    ps.setString(3, store.getRepresentative());
                    ps.setString(4, store.getStoreAddress());
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });

            // ✅ PK 조회: 최근 INSERT된 ID 범위 가져오기
            List<Long> storeIds = getInsertedStoreIds(batchList.size());

            // ✅ 새 Store 객체 생성 (ID 포함)
            for (int i = 0; i < batchList.size(); i++) {
                Store store = batchList.get(i);
                insertedStores.add(
                        Store.builder()
                                .storeId(storeIds.get(i))  // 삽입된 ID 적용
                                .storeName(store.getStoreName())
                                .contactNumber(store.getContactNumber())
                                .representative(store.getRepresentative())
                                .storeAddress(store.getStoreAddress())
                                .build()
                );
            }
        }
        return insertedStores;
    }

    // ✅ 삽입된 ID 목록 가져오기
    private List<Long> getInsertedStoreIds(int count) {
        String sql = "SELECT LAST_INSERT_ID() AS first_id, LAST_INSERT_ID() + ? - 1 AS last_id";

        return jdbcTemplate.query(sql, rs -> {
            List<Long> ids = new ArrayList<>();
            if (rs.next()) {
                long firstId = rs.getLong("first_id");
                long lastId = rs.getLong("last_id");
                for (long i = firstId; i <= lastId; i++) {
                    ids.add(i);
                }
            }
            return ids;
        }, count);
    }
}