package dutchiepay.backend.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.entity.QBuy;
import dutchiepay.backend.entity.QOrder;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QOrderRepositoryImpl implements QOrderRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QOrder order = QOrder.order;
    QBuy buy = QBuy.buy;

    @Override
    public Long countByUserPurchase(User user, String state) {
        return jpaQueryFactory
                .select(order.count())
                .from(order)
                .join(order.buy, buy)
                .where(order.user.eq(user))
                .where(order.state.eq(state))
                .where(order.deletedAt.isNull())
                .where(buy.deletedAt.isNull())
                .orderBy(order.createdAt.desc())
                .fetchOne();
    }
}
