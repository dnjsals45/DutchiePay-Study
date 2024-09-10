package dutchiepay.backend.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.entity.QBuy;
import dutchiepay.backend.entity.QOrders;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QOrderRepositoryImpl implements QOrderRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QOrders orders = QOrders.orders;
    QBuy buy = QBuy.buy;

    @Override
    public Long countByUserPurchase(User user, String state) {
        return jpaQueryFactory
                .select(orders.count())
                .from(orders)
                .join(orders.buy, buy)
                .where(orders.user.eq(user))
                .where(orders.state.eq(state))
                .where(orders.deletedAt.isNull())
                .where(buy.deletedAt.isNull())
                .orderBy(orders.createdAt.desc())
                .fetchOne();
    }
}
