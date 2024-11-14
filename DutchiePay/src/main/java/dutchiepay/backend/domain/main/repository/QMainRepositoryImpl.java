package dutchiepay.backend.domain.main.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.entity.QBuy;
import dutchiepay.backend.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QMainRepositoryImpl implements QMainRepository {

    private final JPAQueryFactory jpaQueryFactory;
    QBuy buy = QBuy.buy;
    QProduct product = QProduct.product;


    @Override
    public List<Tuple> getNewProducts() {

        return jpaQueryFactory
                .select(
                        buy.buyId,
                        product.productName,
                        product.productImg,
                        product.originalPrice,
                        product.salePrice,
                        product.discountPercent,
                        buy.deadline
                )
                .from(buy)
                .join(buy.product, product)
                .orderBy(buy.createdAt.desc())
                .limit(4)
                .fetch();
    }

    @Override
    public List<Tuple> getRecommends() {

        return jpaQueryFactory
                .select(
                        buy.buyId,
                        product.productName,
                        product.productImg,
                        product.originalPrice,
                        product.salePrice,
                        product.discountPercent,
                        buy.deadline
                )
                .from(buy)
                .join(buy.product, product)
                .where(buy.deadline.after(LocalDate.now()))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(8)
                .fetch();
    }

    @Override
    public List<Tuple> getNowHot() {

        return jpaQueryFactory
                .select(
                        buy.buyId,
                        product.productName,
                        product.productImg,
                        product.originalPrice,
                        product.salePrice,
                        product.discountPercent,
                        buy.skeleton,
                        buy.nowCount,
                        buy.deadline
                )
                .from(buy)
                .join(buy.product, product)
                .where(buy.deadline.after(LocalDate.now()))
                .orderBy(buy.nowCount.desc())
                .limit(10)
                .fetch();
    }
}
