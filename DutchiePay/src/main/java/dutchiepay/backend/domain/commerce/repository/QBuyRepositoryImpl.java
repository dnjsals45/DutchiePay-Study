package dutchiepay.backend.domain.commerce.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.commerce.dto.GetBuyListResponseDto;
import dutchiepay.backend.domain.commerce.dto.GetBuyResponseDto;
import dutchiepay.backend.domain.commerce.dto.GetProductReviewResponseDto;
import dutchiepay.backend.domain.commerce.exception.CommerceErrorCode;
import dutchiepay.backend.domain.commerce.exception.CommerceException;
import dutchiepay.backend.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QBuyRepositoryImpl implements QBuyRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QUser user = QUser.user;
    QBuy buy = QBuy.buy;
    QProduct product = QProduct.product;
    QStore store = QStore.store;
    QReview review = QReview.review;
    QLikes likes = QLikes.likes;
    QAsk ask = QAsk.ask;
    QScore score = QScore.score;
    QBuyCategory buyCategory = QBuyCategory.buyCategory;
    QCategory category = QCategory.category;

    @Override
    public GetBuyResponseDto getBuyPageByBuyId(User user, Long buyId) {
        JPAQuery<Tuple> query = jpaQueryFactory
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
                        user != null ?
                                JPAExpressions
                                        .selectOne()
                                        .from(likes)
                                        .where(likes.user.eq(user)
                                                .and(likes.buy.buyId.eq(buyId)))
                                        .exists()
                                : Expressions.nullExpression(),
                        JPAExpressions
                                .select(review.count())
                                .from(review)
                                .where(review.order.buy.buyId.eq(buyId)),
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
                .where(buy.buyId.eq(buyId));

        Tuple result = query.fetchOne();

        if (result == null) {
            return null;
        }

        Integer one = Optional.ofNullable(result.get(17, Integer.class)).orElse(0);
        Integer two = Optional.ofNullable(result.get(18, Integer.class)).orElse(0);
        Integer three = Optional.ofNullable(result.get(19, Integer.class)).orElse(0);
        Integer four = Optional.ofNullable(result.get(20, Integer.class)).orElse(0);
        Integer five = Optional.ofNullable(result.get(21, Integer.class)).orElse(0);

        Integer[] ratingCount = new Integer[]{one, two, three, four, five};

        double average;

        if (one + two + three + four + five == 0) {
            average = 0.0;
        } else {
            average = (one + two * 2 + three * 3 + four * 4 + five * 5) / (double) (one + two + three + four + five);
        }

        List<String> categoryList = jpaQueryFactory
                .select(category.name)
                .from(buyCategory)
                .join(buyCategory.category, category)
                .where(buyCategory.buy.buyId.eq(buyId))
                .fetch();

        String[] categories = categoryList.toArray(new String[0]);

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
                .likeCount(result.get(13, Long.class))
                .isLiked(user != null ? result.get(14, Boolean.class) : null)
                .reviewCount(result.get(15, Long.class))
                .askCount(result.get(16, Long.class))
                .ratingCount(ratingCount)
                .rating(average)
                .category(categories)
                .build();
    }

    @Override
    public GetBuyListResponseDto getBuyList(User user, String filter, String categoryName, int end, Long cursor, int limit) {
        if (cursor == null) {
            cursor = Long.MAX_VALUE;
        }

        BooleanExpression conditions = null;

        if (categoryName != null && !categoryName.isEmpty()) {
            conditions = JPAExpressions
                    .select(buyCategory.buy)
                    .from(buyCategory)
                    .where(buyCategory.category.name.eq(categoryName))
                    .contains(buy);
        }

        if (end == 0) {
            BooleanExpression dateCondition = buy.deadline.after(LocalDate.now());
            conditions = conditions == null ? dateCondition : conditions.and(dateCondition);
        }

        OrderSpecifier<?> orderBy;
        BooleanExpression cursorCondition = null;
        switch (filter) {
            case "like":
                orderBy = likes.count().desc();
                if (cursor < Long.MAX_VALUE) {
                    Long cursorLike = jpaQueryFactory
                            .select(likes.count())
                            .from(likes)
                            .join(likes.buy, buy)
                            .where(buy.buyId.eq(cursor))
                            .fetchOne();

                    if (cursorLike != null) {
                        cursorCondition = likes.count().lt(cursorLike)
                                .or(likes.count().eq(cursorLike))
                                .and(buy.buyId.loe(cursor));
                    }
                }
                break;
            case "endDate":
                orderBy = buy.deadline.asc();
                if (cursor < Long.MAX_VALUE) {
                    LocalDate cursorEndDate = jpaQueryFactory
                            .select(buy.deadline)
                            .from(buy)
                            .where(buy.buyId.eq(cursor))
                            .fetchOne();

                    if (cursorEndDate != null) {
                        cursorCondition = buy.deadline.gt(cursorEndDate)
                                .or(buy.deadline.eq(cursorEndDate))
                                .and(buy.buyId.loe(cursor));
                    }
                }
                break;
            case "discount":
                if (cursor < Long.MAX_VALUE) {
                    Integer cursorDiscount = jpaQueryFactory
                            .select(product.discountPercent)
                            .from(buy)
                            .join(buy.product, product)
                            .where(buy.buyId.eq(cursor))
                            .fetchOne();

                    if (cursorDiscount != null) {
                        cursorCondition = product.discountPercent.lt(cursorDiscount)
                                .or(product.discountPercent.eq(cursorDiscount));
                    }
                }
                orderBy = product.discountPercent.desc();
                break;
            case "newest":
                orderBy = buy.buyId.desc();
                conditions = conditions != null ? conditions.and(buy.buyId.loe(cursor)) : null;
                break;
            default:
                throw new CommerceException(CommerceErrorCode.INVALID_FILTER);
        }

        JPAQuery<Tuple> query = jpaQueryFactory
                .select(buy.buyId,
                        product.productName,
                        product.productImg,
                        product.originalPrice,
                        product.salePrice,
                        product.discountPercent,
                        buy.skeleton,
                        buy.nowCount,
                        buy.deadline)
                .from(buy)
                .join(buy.product, product)
                .leftJoin(buyCategory).on(buyCategory.buy.eq(buy))
                .leftJoin(category).on(buyCategory.category.eq(category));

        if (user != null) {
            query.leftJoin(likes).on(likes.buy.eq(buy).and(likes.user.eq(user)))
                    .select(likes.count().gt(0L).as("isLiked"));
        }

        query.where(conditions)
                .groupBy(buy.buyId, product.productName, product.productImg, product.originalPrice,
                        product.salePrice, product.discountPercent, buy.skeleton, buy.nowCount, buy.deadline)
                .limit(limit + 1);

        if (filter.equals("like")) {
            query.having(cursorCondition);
            query.orderBy(orderBy, buy.buyId.desc());
        } else {
            query.where(cursorCondition);
            query.orderBy(orderBy, buy.buyId.desc());
        }

        List<Tuple> results = query.fetch();
        List<GetBuyListResponseDto.ProductDto> products = new ArrayList<>();
        int count = 0;
        for (Tuple result : results) {
            if (count >= limit) {
                break;
            }

            GetBuyListResponseDto.ProductDto.ProductDtoBuilder dtoBuilder = GetBuyListResponseDto.ProductDto.builder()
                    .buyId(result.get(0, Long.class))
                    .productName(result.get(1, String.class))
                    .productImg(result.get(2, String.class))
                    .productPrice(result.get(3, Integer.class))
                    .discountPrice(result.get(4, Integer.class))
                    .discountPercent(result.get(5, Integer.class))
                    .skeleton(result.get(6, Integer.class))
                    .nowCount(result.get(7, Integer.class))
                    .expireDate(calculateExpireDate(result.get(8, LocalDate.class)));

            if (user != null) {
                dtoBuilder.isLiked(result.get(9, Boolean.class));
            }
            products.add(dtoBuilder.build());
            count++;
        }

        Long nextCursor = results.size() > limit ? results.get(limit).get(buy.buyId) : null;

        return GetBuyListResponseDto.builder()
                .products(products)
                .cursor(nextCursor)
                .build();
    }

    @Override
    public GetProductReviewResponseDto getProductReview(Long buyId, Long photo, PageRequest pageable) {
        List<Tuple> result = jpaQueryFactory
                .select(review.reviewId,
                        review.user.nickname,
                        review.contents,
                        review.rating,
                        review.reviewImg,
                        review.createdAt)
                .from(review)
                .join(review.user, user)
                .join(review.order.buy, buy)
                .where(buy.buyId.eq(buyId))
                .where(photoCondition(photo))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<GetProductReviewResponseDto.ReviewDto> reviews = new ArrayList<>();

        for (Tuple tuple : result) {
            GetProductReviewResponseDto.ReviewDto reviewDto = GetProductReviewResponseDto.ReviewDto.builder()
                    .reviewId(tuple.get(0, Long.class))
                    .nickname(tuple.get(1, String.class))
                    .content(tuple.get(2, String.class))
                    .rating(tuple.get(3, Integer.class))
                    .reviewImg(tuple.get(4, String.class) != null ? tuple.get(4, String.class).split(",") : new String[0])
                    .createdAt(tuple.get(5, LocalDateTime.class))
                    .build();

            reviews.add(reviewDto);
        }

        Double avgRating = jpaQueryFactory
                .select(review.rating.avg())
                .from(review)
                .join(review.order.buy, buy)
                .where(buy.buyId.eq(buyId))
                .fetchOne();

        return GetProductReviewResponseDto.from(avgRating, reviews);
    }

    private int calculateExpireDate(LocalDate deadline) {
        if (deadline.isBefore(LocalDate.now())) {
            return -1;
        } else if (deadline.isEqual(LocalDate.now())) {
            return 0;
        } else {
            return (int) ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        }
    }

    private BooleanExpression photoCondition(Long photo) {
        if (photo == null) {
            return null;
        }
        return photo == 1 ? review.reviewImg.isNotNull() : null;
    }
}