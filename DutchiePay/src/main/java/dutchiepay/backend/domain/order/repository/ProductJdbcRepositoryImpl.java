package dutchiepay.backend.domain.order.repository;

import dutchiepay.backend.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductJdbcRepositoryImpl {
    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    @Transactional
    public List<Product> bulkInsert(List<Product> products) {
        String sql = """
            INSERT INTO product (store_id, product_name, detail_img, original_price, sale_price, discount_percent, product_img, created_at, updated_at, deleted_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NULL)
        """;

        List<Product> insertedProducts = new ArrayList<>();

        for (int start = 0; start < products.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, products.size());
            List<Product> batchList = products.subList(start, end);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Product product = batchList.get(i);
                    ps.setLong(1, product.getStore().getStoreId());
                    ps.setString(2, product.getProductName());
                    ps.setString(3, product.getDetailImg());
                    ps.setInt(4, product.getOriginalPrice());
                    ps.setInt(5, product.getSalePrice());
                    ps.setInt(6, product.getDiscountPercent());
                    ps.setString(7, product.getProductImg());
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });

            // ✅ PK 조회: 최근 INSERT된 ID 가져오기
            List<Long> productIds = getInsertedProductIds(batchList.size());

            // ✅ 새 Product 객체 생성 (ID 포함)
            for (int i = 0; i < batchList.size(); i++) {
                Product product = batchList.get(i);
                insertedProducts.add(
                        Product.builder()
                                .productId(productIds.get(i))  // 삽입된 ID 적용
                                .store(product.getStore())
                                .productName(product.getProductName())
                                .detailImg(product.getDetailImg())
                                .originalPrice(product.getOriginalPrice())
                                .salePrice(product.getSalePrice())
                                .discountPercent(product.getDiscountPercent())
                                .productImg(product.getProductImg())
                                .build()
                );
            }
        }
        return insertedProducts;
    }

    // ✅ 삽입된 ID 목록 가져오기
    private List<Long> getInsertedProductIds(int count) {
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