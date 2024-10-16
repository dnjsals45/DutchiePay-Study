package dutchiepay.backend.domain.commerce.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.commerce.dto.GetBuyResponseDto;
import dutchiepay.backend.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
@RequiredArgsConstructor
@Slf4j
public class QBuyRepositoryImpl implements QBuyRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QBuy buy = QBuy.buy;
    QProduct product = QProduct.product;
    QStore store = QStore.store;
    QReview review = QReview.review;
    QLikes likes = QLikes.likes;
    QAsk ask = QAsk.ask;
    QScore score = QScore.score;

    @Override
    public GetBuyResponseDto getBuyPageByBuyId(Long userId, Long buyId) {
        Tuple result = jpaQueryFactory
                .select(
                        product.productName,
                        product.productImg,
                        product.detailImg,
                        product.originalPrice,
                        product.salePrice,
                        product.discountPercent,
                        store.storeName,
                        store.contactNumber,
                        store.representative,
                        store.storeAddress,
                        buy.skeleton,
                        buy.nowCount,
                        buy.deadline,
                        JPAExpressions
                                .select(likes.count())
                                .from(likes)
                                .where(likes.buy.buyId.eq(buyId)),
                        JPAExpressions
                                .selectOne()
                                .from(likes)
                                .where(likes.user.userId.eq(userId)
                                        .and(likes.buy.buyId.eq(buyId)))
                                .exists(),
                        JPAExpressions
                                .select(review.count())
                                .from(review)
                                .where(review.buy.buyId.eq(buyId)),
                        JPAExpressions
                                .select(ask.count())
                                .from(ask)
                                .where(ask.buy.buyId.eq(buyId)),
                        score.one,
                        score.two,
                        score.three,
                        score.four,
                        score.five
                )
                .from(buy)
                .join(buy.product, product)
                .join(product.store, store)
                .leftJoin(score).on(score.buy.buyId.eq(buy.buyId))
                .where(buy.buyId.eq(buyId))
                .fetchOne();

        if (result == null) {
            return null;
        }

        Integer[] ratingCount = new Integer[]{
                result.get(17, Integer.class),
                result.get(18, Integer.class),
                result.get(19, Integer.class),
                result.get(20, Integer.class),
                result.get(21, Integer.class)
        };

        return GetBuyResponseDto.builder()
                .productName(result.get(0, String.class))
                .productImg(result.get(1, String.class))
                .productDetail(result.get(2, String.class))
                .originalPrice(result.get(3, Integer.class))
                .salePrice(result.get(4, Integer.class))
                .discountPercent(result.get(5, Integer.class))
                .storeName(result.get(6, String.class))
                .contactNumber(result.get(7, String.class))
                .representative(result.get(8, String.class))
                .storeAddress(result.get(9, String.class))
                .skeleton(result.get(10, Integer.class))
                .nowCount(result.get(11, Integer.class))
                .deadline(result.get(12, LocalDate.class))
                .likeCount(result.get(13, Long.class).intValue())
                .isLiked(result.get(14, Boolean.class))
                .reviewCount(result.get(15, Long.class).intValue())
                .askCount(result.get(16, Long.class).intValue())
                .ratingCount(ratingCount)
                .build();
    }
}
