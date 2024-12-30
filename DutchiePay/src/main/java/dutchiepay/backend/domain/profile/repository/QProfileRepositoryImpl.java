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
import dutchiepay.backend.domain.profile.exception.ProfileErrorCode;
import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
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
    QReview review = QReview.review;


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
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(review.count().castToNum(Integer.class))
                                        .from(review)
                                        .where(review.order.buy.eq(buy)), "reviewCount"
                        ),
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
                    .productPrice(tuple.get(2, Integer.class))
                    .discountPrice(tuple.get(3, Integer.class))
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
    public MyGoodsResponseDto getMyGoods(User user, String filter, Pageable pageable) {
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
                        orders.message,
                        product.productImg,
                        store.storeName)
                .from(orders)
                .join(orders.product, product)
                .join(product.store, store)
                .where(orders.user.eq(user), filterCondition)
                .orderBy(orders.orderedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        List<Tuple> content = tuple;
        if (tuple.size() > pageable.getPageSize()) {
            hasNext = true;
            content = tuple.subList(0, pageable.getPageSize());
        }

        List<MyGoodsResponseDto.Goods> result = new ArrayList<>();

        if (pageable.getPageNumber() == 0 && tuple.isEmpty()) {
            throw new ProfileErrorException(ProfileErrorCode.NO_HISTORY_ORDER);
        } else if (tuple.isEmpty()) {
            throw new ProfileErrorException(ProfileErrorCode.NO_MORE_HISTORY_ORDER);
        }


        for (Tuple t : content) {
            MyGoodsResponseDto.Goods dto = MyGoodsResponseDto.Goods.builder()
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

        return MyGoodsResponseDto.builder()
                .goods(result)
                .hasNext(hasNext)
                .build();
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

    @Override
    public MyPostsResponseDto getMyPosts(User user, PageRequest pageable) {
        String sql = "SELECT " +
                "share.share_id as postId, " +
                "share.title, " +
                "share.created_at as writeTime, " +
                "share.description as content, " +
                "'마트/배달' as category, " +
                "NULL as commentCount, " +
                "share.thumbnail, " +
                "users.nickname as writerNickname, " +
                "users.profile_img as writerProfileImage " +
                "FROM share " +
                "LEFT JOIN users ON share.user_id = users.user_id " +
                "WHERE share.user_id = :userId AND share.deleted_at IS NULL " +
                "UNION ALL " +
                "SELECT " +
                "free.free_id as postId, " +
                "free.title, " +
                "free.created_at as writeTime, " +
                "free.description as content, " +
                "'자유' as category, " +
                "(SELECT COUNT(*) FROM comment WHERE free_id = free.free_id AND deleted_at IS NULL) as commentCount, " +
                "NULL as thumbnail, " +
                "users.nickname as writerNickname, " +
                "users.profile_img as writerProfileImage " +
                "FROM free " +
                "LEFT JOIN users ON free.user_id = users.user_id " +
                "WHERE free.user_id = :userId AND free.deleted_at IS NULL " +
                "ORDER BY writeTime DESC " +
                "LIMIT :limit OFFSET :offset";

        Query query = entityManager.createNativeQuery(sql)
                .setParameter("userId", user.getUserId())
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset());

        String countSql = "SELECT COUNT(*) FROM (" +
                "SELECT share_id FROM share WHERE user_id = :userId AND deleted_at IS NULL " +
                "UNION ALL " +
                "SELECT free_id FROM free WHERE user_id = :userId AND deleted_at IS NULL" +
                ") AS combined_posts";

        Query countQuery = entityManager.createNativeQuery(countSql)
                .setParameter("userId", user.getUserId());
        Integer totalPostCount = ((Number) countQuery.getSingleResult()).intValue();

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();

        List<MyPostsResponseDto.Post> result = new ArrayList<>();

        for (Object[] objects : resultList) {
            LocalDateTime dataTime = ((Timestamp) objects[2]).toLocalDateTime();

            String writeTime = convertRelativeTime(dataTime);

            MyPostsResponseDto.Post data = MyPostsResponseDto.Post.builder()
                                    .postId((Long) objects[0])
                                    .title((String) objects[1])
                                    .writeTime(writeTime)
                                    .description((String) objects[3])
                                    .category((String) objects[4])
                                    .commentCount((Long) objects[5])
                                    .thumbnail((String) objects[6])
                                    .writerNickname((String) objects[7])
                                    .writerProfileImage((String) objects[8])
                                    .build();

            result.add(data);
        }

        return MyPostsResponseDto.builder()
                .totalPost(totalPostCount)
                .posts(result)
                .build();
    }

    @Override
    public MyPostsResponseDto getMyCommentsPosts(User user, PageRequest pageable) {
        List<Tuple> queryResult = jpaQueryFactory
                .select(
                        free.freeId,
                        free.title,
                        free.createdAt,
                        free.description,
                        free.category,
                        free.thumbnail,
                        free.user.nickname,
                        free.user.profileImg,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(comment.count())
                                        .from(comment)
                                        .where(comment.free.eq(free))
                                        .where(comment.deletedAt.isNull()), "commentCount"))
                .from(free)
                .leftJoin(free.user)
                .leftJoin(comment).on(comment.free.eq(free).and(comment.deletedAt.isNull()))
                .where(comment.user.eq(user))
                .groupBy(free.freeId)
                .orderBy(free.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalPostCount = jpaQueryFactory
                .select(free.freeId.countDistinct())
                .from(free)
                .innerJoin(comment).on(comment.free.eq(free).and(comment.deletedAt.isNull()))
                .where(comment.user.eq(user))
                .fetchOne();

        List<MyPostsResponseDto.Post> result = new ArrayList<>();
        for (Tuple tuple : queryResult) {
            System.out.println("===========================================");
            System.out.println("tuple = " + tuple);
            System.out.println("===========================================");
            LocalDateTime dbTime = tuple.get(free.createdAt);

            long daysBetween = ChronoUnit.DAYS.between(dbTime, LocalDateTime.now());
            String writeTime = daysBetween + "일 전";

            MyPostsResponseDto.Post data = MyPostsResponseDto.Post.builder()
                    .postId(tuple.get(free.freeId))
                    .title(tuple.get(free.title))
                    .writeTime(writeTime)
                    .description(tuple.get(free.description))
                    .category(tuple.get(free.category))
                    .commentCount(tuple.get(8, Long.class))
                    .thumbnail(tuple.get(free.thumbnail))
                    .writerNickname(tuple.get(free.user.nickname))
                    .writerProfileImage(tuple.get(free.user.profileImg))
                    .build();

            result.add(data);
        }

        return MyPostsResponseDto.builder()
                .totalPost(totalPostCount.intValue())
                .posts(result)
                .build();
    }

    private String convertRelativeTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);
        long weeks = ChronoUnit.WEEKS.between(createdAt, now);
        long months = ChronoUnit.MONTHS.between(createdAt, now);
        long years = ChronoUnit.YEARS.between(createdAt, now);

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 7) {
            return days + "일 전";
        } else if (days < 30) {
            return weeks + "주 전";
        } else if (days < 365) {
            return months + "달 전";
        } else {
            return years + "년 전";
        }
    }
}
