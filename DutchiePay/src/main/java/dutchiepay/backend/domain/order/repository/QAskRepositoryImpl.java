package dutchiepay.backend.domain.order.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.profile.dto.GetMyAskResponseDto;
import dutchiepay.backend.entity.QAsk;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QAskRepositoryImpl implements QAskRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QAsk ask = QAsk.ask;

    @Override
    public List<GetMyAskResponseDto> getMyAsks(User user) {
        List<Tuple> tuple = jpaQueryFactory
                .select(
                        ask.askId,
                        ask.product.store.storeName,
                        ask.buy.buyId,
                        ask.product.productName,
                        ask.contents,
                        ask.answer,
                        ask.createdAt,
                        ask.answeredAt,
                        ask.secret
                )
                .from(ask)
                .join(ask.product)
                .join(ask.buy)
                .join(ask.product.store)
                .where(ask.user.eq(user))
                .where(ask.deletedAt.isNull())
                .fetch();

        List<GetMyAskResponseDto> result = new ArrayList<>();

        if (tuple.isEmpty()) {
            return result;
        }

        for (Tuple t : tuple) {
            result.add(GetMyAskResponseDto.builder()
                    .askId(t.get(ask.askId))
                    .storeName(t.get(ask.product.store.storeName))
                    .buyId(t.get(ask.buy.buyId))
                    .productName(t.get(ask.product.productName))
                    .content(t.get(ask.contents))
                    .answer(t.get(ask.answer))
                    .createdAt(t.get(ask.createdAt))
                    .answeredAt(t.get(ask.answeredAt))
                    .isSecret(t.get(ask.secret))
                    .build());
        }

        return result;
    }
}
