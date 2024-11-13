package dutchiepay.backend.domain.profile.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.profile.dto.GetMyLikesResponseDto;
import dutchiepay.backend.domain.profile.dto.MyGoodsResponseDto;
import dutchiepay.backend.domain.profile.dto.MyPostsResponseDto;
import dutchiepay.backend.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@RequiredArgsConstructor
@Slf4j
public class QProfileRepositoryImpl implements QProfileRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private final EntityManager entityManager;

    QBuy buy = QBuy.buy;
    QProduct product = QProduct.product;
    QScore score = QScore.score;
    QLike like = QLike.like;
    QOrder orders = QOrder.order;
    QStore store = QStore.store;
    QFree free = QFree.free;
    QComment comment = QComment.comment;
    QBuyCategory buyCategory = QBuyCategory.buyCategory;
    QCategory category = QCategory.category;


    @Override
    public List<GetMyLikesResponseDto> getMyLike(User user) {
        LocalDate now = LocalDate.now();

        List<Tuple> query = jpaQueryFactory
                .select(buy.buyId,
                        product.productName,
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
                                        .where(score.buy.eq(buy)), "rating"
                        ),
                        score.count,
                        ExpressionUtils.as(
                                Expressions.numberTemplate(
                                        Integer.class,
                                        "case when DATEDIFF({0}, {1}) < 0 then -1 else cast(DATEDIFF({0}, {1}) as integer) end",
                                        buy.deadline, now
                                ),
                                "expireDate"
                        ),
                        buy.skeleton,
                        buy.nowCount
                )
                .from(like)
                .join(like.buy, buy)
                .join(buy.product, product)
                .leftJoin(score).on(score.buy.eq(buy))
                .where(buy.deletedAt.isNull())
                .where(like.user.eq(user))
                .orderBy(like.createdAt.desc())
                .fetch();

        List<Long> buyIds = new ArrayList<>();
        for (Tuple tuple : query) {
            buyIds.add(tuple.get(0, Long.class));
        }

        Map<Long, List<String>> categoryMap = new HashMap<>();
        List<Tuple> categoryList = jpaQueryFactory
                .select(buy.buyId, category.name)
                .from(buyCategory)
                .join(buyCategory.buy, buy)
                .join(buyCategory.category, category)
                .where(buy.buyId.in(buyIds))
                .fetch();

        for (Tuple tuple : categoryList) {
            Long buyId = tuple.get(0, Long.class);
            String categoryName = tuple.get(1, String.class);

            if (!categoryMap.containsKey(buyId)) {
                categoryMap.put(buyId, new ArrayList<>());
            }
            categoryMap.get(buyId).add(categoryName);
        }

        List<GetMyLikesResponseDto> response = new ArrayList<>();
        for (Tuple tuple : query) {
            Long buyId = tuple.get(0, Long.class);
            List<String> categories = categoryMap.getOrDefault(buyId, new ArrayList<>());

            GetMyLikesResponseDto dto = GetMyLikesResponseDto.builder()
                    .buyId(buyId)
                    .category(categories.toArray(new String[0]))
                    .productName(tuple.get(1, String.class))
                    .originalPrice(tuple.get(2, Integer.class))
                    .salePrice(tuple.get(3, Integer.class))
                    .discountPercent(tuple.get(4, Integer.class))
                    .productImg(tuple.get(5, String.class))
                    .rating(tuple.get(6, Double.class))
                    .reviewCount(tuple.get(7, Integer.class))
                    .expireDate(tuple.get(8, Integer.class))
                    .skeleton(tuple.get(9, Integer.class))
                    .nowCount(tuple.get(10, Integer.class))
                    .build();

            response.add(dto);
        }

        return response;
    }

    @Override
    public List<MyGoodsResponseDto> getMyGoods(User user, String filter, Pageable pageable) {

        BooleanExpression filterCondition = getMyGoodsFilterCondition(filter);

        List<Tuple> tuple =  jpaQueryFactory
                .select(orders.orderId,
                        orders.orderNum,
                        buy.buyId,
                        orders.orderedAt,
                        product.productName,
                        orders.quantity,
                        product.originalPrice,
                        product.salePrice,
                        orders.totalPrice,
                        orders.payment,
                        orders.receiver,
                        orders.address,
                        orders.zipCode,
                        orders.detail,
                        orders.state,
                        product.productImg,
                        store.storeName)
                .from(orders)
                .join(orders.product, product)
                .join(product.store, store)
                .where(orders.user.eq(user), filterCondition)
                .orderBy(orders.orderedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<MyGoodsResponseDto> result = new ArrayList<>();

        if (tuple.isEmpty()) {
            return result;
        }

        for (Tuple t : tuple) {
            MyGoodsResponseDto dto = MyGoodsResponseDto.builder()
                    .orderId(t.get(orders.orderId))
                    .orderNum(t.get(orders.orderNum))
                    .buyId(t.get(buy.buyId))
                    .orderDate(t.get(orders.orderedAt).toLocalDate())
                    .productName(t.get(product.productName))
                    .quantity(t.get(orders.quantity))
                    .productPrice(t.get(product.originalPrice))
                    .discountPrice(t.get(product.salePrice))
                    .totalPrice(t.get(orders.totalPrice))
                    .payment(t.get(orders.payment))
                    .receiver(t.get(orders.receiver))
                    .address(t.get(orders.address))
                    .zipCode(t.get(orders.zipCode))
                    .detail(t.get(orders.detail))
                    .deliveryState(t.get(orders.state))
                    .productImg(t.get(product.productImg))
                    .storeName(t.get(store.storeName))
                    .message(t.get(orders.message))
                    .build();

            result.add(dto);
        }

        return result;
    }

    private BooleanExpression getMyGoodsFilterCondition(String filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }

        switch (filter) {
            case "pending":
                return orders.state.in(
                        "공구진행중",
                        "배송준비중",
                        "공구실패",
                        "구매취소"
                );

            case "shipped":
                return orders.state.in(
                        "배송중"
                );

            case "delivered":
                return orders.state.in(
                        "배송완료",
                        "구매확정",
                        "환불/교환"
                );
            default:
                return null;
        }
    }

    // QueryDsl 은 union 작성이 안된다.. native로 작성해야할까?
    @Override
    public List<MyPostsResponseDto> getMyPosts(User user, PageRequest pageable) {
//        List<MyPostsResponseDto> shareResult = jpaQueryFactory
//                .select(Projections.constructor(MyPostsResponseDto.class,
//                        share.shareId.as("postId"),
//                        share.title.as("title"),
//                        share.createdAt.as("writeTime"),
//                        share.contents.as("content"),
//                        Expressions.asString("마트/배달").as("category"),
//                        Expressions.nullExpression(String.class),
//                        share.thumbnail.as("thumbnail")))
//                .from(share)
//                .where(share.userId.eq(user))
//                .where(share.deletedAt.isNull())
//                .orderBy(share.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        List<MyPostsResponseDto> freeResult = jpaQueryFactory
//                .select(Projections.constructor(MyPostsResponseDto.class,
//                        free.freeId.as("postId"),
//                        free.title.as("title"),
//                        free.createdAt.as("writeTime"),
//                        free.contents.as("content"),
//                        Expressions.asString("자유").as("category"),
//                        ExpressionUtils.as(
//                                JPAExpressions
//                                        .select(comment.count())
//                                        .from(comment)
//                                        .where(comment.freeId.eq(free))
//                                        .where(comment.deletedAt.isNull()), "commentCount"),
//                        Expressions.nullExpression(String.class)))
//                .from(free)
//                .where(free.user.eq(user))
//                .where(free.deletedAt.isNull())
//                .orderBy(free.createdAt.desc())
//                .offset(pageable.getOffset())
//                .fetch();

        String sql = "SELECT share_id as postId, title, created_at as writeTime, contents as content, '마트/배달' as category, NULL as commentCount, thumbnail " +
                "FROM share " +
                "WHERE user_id = :userId AND deleted_at IS NULL " +
                "UNION ALL " +
                "SELECT free_id as postId, title, created_at as writeTime, contents as content, '자유' as category, " +
                "(SELECT COUNT(*) FROM comment WHERE free_id = free.free_id AND deleted_at IS NULL) as commentCount, NULL as thumbnail " +
                "FROM free " +
                "WHERE user_id = :userId AND deleted_at IS NULL " +
                "ORDER BY writeTime DESC " +
                "LIMIT :limit OFFSET :offset";

        Query query = entityManager.createNativeQuery(sql)
                .setParameter("userId", user.getUserId())
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();

        List<MyPostsResponseDto> result = new ArrayList<>();

        for (Object[] objects : resultList) {
            LocalDateTime dbTime = ((Timestamp) objects[2]).toLocalDateTime();

            long daysBetween = ChronoUnit.DAYS.between(dbTime, LocalDateTime.now());
            String writeTime = daysBetween + "일 전";

            MyPostsResponseDto data = MyPostsResponseDto.builder()
                                    .postId((Long) objects[0])
                                    .title((String) objects[1])
                                    .writeTime(writeTime)
                                    .content((String) objects[3])
                                    .category((String) objects[4])
                                    .commentCount((Long) objects[5])
                                    .thumbnail((String) objects[6])
                                    .build();

            result.add(data);
        }

        return result;
    }

    @Override
    public List<MyPostsResponseDto> getMyCommentsPosts(User user, PageRequest pageable) {
        List<Tuple> queryResult = jpaQueryFactory
                .select(
                        free.freeId,
                        free.title,
                        free.createdAt,
                        free.contents,
                        free.category,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(comment.count())
                                        .from(comment)
                                        .where(comment.free.eq(free))
                                        .where(comment.deletedAt.isNull()), "commentCount"),
                        Expressions.nullExpression(String.class))
                .from(free)
                .leftJoin(comment).on(comment.free.eq(free).and(comment.deletedAt.isNull()))
                .where(comment.user.eq(user))
                .groupBy(free.freeId)
                .orderBy(free.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<MyPostsResponseDto> result = new ArrayList<>();
        for (Tuple tuple : queryResult) {
            LocalDateTime dbTime = tuple.get(free.createdAt);

            long daysBetween = ChronoUnit.DAYS.between(dbTime, LocalDateTime.now());
            String writeTime = daysBetween + "일 전";

            MyPostsResponseDto data = MyPostsResponseDto.builder()
                    .postId(tuple.get(free.freeId))
                    .title(tuple.get(free.title))
                    .writeTime(writeTime)
                    .content(tuple.get(free.contents))
                    .category(tuple.get(free.category))
                    .commentCount(tuple.get(5, Long.class))
                    .thumbnail(null)
                    .build();

            result.add(data);
        }

        return result;
    }

    private BooleanExpression categoryEq(String categoryName) {
        return category != null ? category.name.eq(categoryName) : null;
    }
}
