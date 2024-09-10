package dutchiepay.backend.domain.profile.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.commerce.BuyCategory;
import dutchiepay.backend.domain.profile.dto.GetMyLikesResponseDto;
import dutchiepay.backend.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
@RequiredArgsConstructor
@Slf4j
public class QProfileRepositoryImpl implements QProfileRepository {

    private final JPAQueryFactory jpaQueryFactory;

    QBuy buy = QBuy.buy;
    QProduct product = QProduct.product;
    QScore score = QScore.score;
    QLikes like = QLikes.likes;

    @Override
    public List<GetMyLikesResponseDto> getMyLike(User user, String category) {
        LocalDate now = LocalDate.now();

        System.out.println("category = " + category);
        return jpaQueryFactory
                .select(Projections.constructor(GetMyLikesResponseDto.class,
                        buy.category,
                        buy.title,
                        product.originalPrice,
                        product.salePrice,
                        product.discountPercent,
                        product.productImg,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(score.one.multiply(1)
                                            .add(score.two.multiply(2))
                                            .add(score.three.multiply(3))
                                            .add(score.four.multiply(4))
                                            .add(score.five.multiply(5))
                                            .divide(score.count.coalesce(1)
                                            .castToNum(Double.class)))
                                        .from(score)
                                        .where(score.buy.eq(buy)),"average"
                        ),
                        score.count,
                        ExpressionUtils.as(
                                Expressions.numberTemplate(
                                        Integer.class,
                                        "case when DATEDIFF({0}, {1}) < 0 then -1 else cast(DATEDIFF({0}, {1}) as integer) end",
                                        buy.deadline, now
                                ),
                                "expireDate"
                        )
                        ))
                .from(like)
                .join(like.buy, buy)
                .join(buy.productId, product)
                .leftJoin(score).on(score.buy.eq(buy))
                .where(buy.deletedAt.isNull())
                .where(like.user.eq(user))
                .where(categoryEq(category))
                .orderBy(like.createdAt.desc())
                .fetch();
    }

    private BooleanExpression categoryEq(String category) {
        return category != null ? buy.category.eq(BuyCategory.fromCategoryName(category)) : null;
    }
}
