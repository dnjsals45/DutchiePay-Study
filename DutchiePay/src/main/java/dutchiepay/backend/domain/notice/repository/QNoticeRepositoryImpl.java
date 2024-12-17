package dutchiepay.backend.domain.notice.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.ChronoUtil;
import dutchiepay.backend.domain.notice.dto.GetNoticeListResponseDto;
import dutchiepay.backend.entity.Notice;
import dutchiepay.backend.entity.QNotice;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QNoticeRepositoryImpl implements QNoticeRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QNotice notice = QNotice.notice;

    @Override
    public List<GetNoticeListResponseDto> findRecentNotices(User user) {
        QNotice subNotice = new QNotice("subNotice");

        List<Tuple> notices = jpaQueryFactory
                .select(
                        notice,
                        JPAExpressions
                                .select(notice.count())
                                .from(notice)
                                .where(
                                        baseNoticeCondition(user),
                                        noticeTypeCondition()
                                )
                )
                .from(notice)
                .where(
                        baseNoticeCondition(user),
                        noticeTypeCondition()
                )
                .groupBy(notice.origin, notice.type)
                .having(
                        notice.noticeId.eq(
                                JPAExpressions
                                        .select(subNotice.noticeId)
                                        .from(subNotice)
                                        .where(
                                                subNotice.user.eq(user),
                                                subNotice.origin.eq(notice.origin),
                                                subNotice.type.eq(notice.type)
                                        )
                                        .orderBy(subNotice.createdAt.desc())
                                        .limit(1)
                        )
                )
                .orderBy(notice.createdAt.desc())
                .fetch();

        List<GetNoticeListResponseDto> result = new ArrayList<>();

        for (Tuple t : notices) {
            Notice n = t.get(0, Notice.class);
            Long originNoticeCount = t.get(1, Long.class);

            GetNoticeListResponseDto dto = GetNoticeListResponseDto.builder()
                    .noticeId(n.getNoticeId())
                    .type(n.getType())
                    .origin(n.getOrigin())
                    .content(n.getContent())
                    .relativeTime(ChronoUtil.formatDateTime(n.getCreatedAt()))
                    .pageId(n.getOriginId())
                    .commentId(n.getCommentId())
                    .hasMore(originNoticeCount > 1)
                    .build();

            result.add(dto);
        }

        return result;
    }

    private BooleanExpression noticeTypeCondition() {
        return notice.type.eq("commerce").and(notice.originId.isNull())
                .or(
                        notice.type.in("comment", "reply", "chat")
                                .and(notice.origin.isNotNull())
                );
    }

    private BooleanExpression baseNoticeCondition(User user) {
        return notice.user.eq(user)
                .and(notice.createdAt.goe(LocalDateTime.now().minusDays(7)))
                .and(notice.deletedAt.isNull());
    }
}
