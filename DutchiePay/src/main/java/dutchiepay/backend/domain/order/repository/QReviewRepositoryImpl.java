package dutchiepay.backend.domain.order.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.profile.dto.GetMyReviewResponseDto;
import dutchiepay.backend.entity.QReview;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QReviewRepositoryImpl implements QReviewRepository {

    private final JPAQueryFactory jpaQueryFactory;

    QReview review = QReview.review;

    @Override
    public List<GetMyReviewResponseDto> getMyReviews(User user) {
        List<Tuple> tuple = jpaQueryFactory
                .select(review.reviewId,
                        review.order.buy.buyId,
                        review.order.product.productName,
                        review.order.orderNum,
                        review.rating,
                        review.contents,
                        review.createdAt,
                        review.updateCount,
                        review.reviewImg)
                .from(review)
                .join(review.order)
                .join(review.order.buy)
                .join(review.order.product)
                .where(review.user.eq(user))
                .where(review.deletedAt.isNull())
                .fetch();

        List<GetMyReviewResponseDto> result = new ArrayList<>();

        if (tuple.isEmpty()) {
            return result;
        }

        for (Tuple t : tuple) {
            LocalDate createdAt = t.get(review.createdAt).toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(createdAt, LocalDate.now());

            result.add(GetMyReviewResponseDto.builder()
                    .reviewId(t.get(review.reviewId))
                    .buyId(t.get(review.order.buy.buyId))
                    .productName(t.get(review.order.product.productName))
                    .orderNum(t.get(review.order.orderNum))
                    .rating(t.get(review.rating))
                    .content(t.get(review.contents))
                    .createdAt(createdAt)
                    .isPossible(daysBetween <= 30 && t.get(review.updateCount) != 3)
                    .reviewImg(t.get(review.reviewImg) != null ? t.get(review.reviewImg).split(",") : new String[0])
                    .build());
        }

        return result;
    }
}
