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

        List<Notice> notices = jpaQueryFactory
                .selectFrom(notice)
                .where(
                        baseNoticeCondition(user),
                        noticeTypeCondition(),
                        JPAExpressions
                                .select(subNotice.createdAt.max())
                                .from(subNotice)
                                .where(
                                        subNotice.user.eq(user),
                                        subNotice.originId.eq(notice.originId),
                                        subNotice.type.eq(notice.type),
//                                        subNotice.isRead.eq(false),
                                        subNotice.createdAt.goe(LocalDateTime.now().minusDays(7)),
                                        subNotice.deletedAt.isNull()
                                )
                                .eq(notice.createdAt)
                )
                .orderBy(notice.createdAt.desc())
                .fetch();

        List<GetNoticeListResponseDto> result = new ArrayList<>();

        for (Notice n : notices) {
            Long originNoticeCount = jpaQueryFactory
                    .select(notice.count())
                    .from(notice)
                    .where(
                            notice.user.eq(user),
                            notice.originId.eq(n.getOriginId()),
                            notice.type.eq(n.getType()),
//                            notice.isRead.eq(false),
                            notice.createdAt.goe(LocalDateTime.now().minusDays(7)),
                            notice.deletedAt.isNull()
                    )
                    .fetchOne();

            GetNoticeListResponseDto dto = GetNoticeListResponseDto.builder()
                    .noticeId(n.getNoticeId())
                    .type(n.getType())
                    .origin(n.getOrigin())
                    .content(n.getContent())
                    .writer(n.getWriter())
                    .relativeTime(ChronoUtil.formatDateTime(n.getCreatedAt()))
                    .pageId(n.getOriginId())
                    .commentId(n.getCommentId())
                    .hasMore(originNoticeCount > 1)
                    .count(originNoticeCount)
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    public List<GetNoticeListResponseDto> getMoreNotices(User user, Long noticeId) {
        Tuple tuple = jpaQueryFactory
                .select(notice.type,
                        notice.originId)
                .from(notice)
                .where(notice.noticeId.eq(noticeId))
                .fetchOne();

        List<Notice> notices = jpaQueryFactory
                .selectFrom(notice)
                .where(
                        notice.type.eq(tuple.get(notice.type)),
                        notice.originId.eq(tuple.get(notice.originId)),
//                                .and(notice.isRead.eq(false))
                        notice.user.eq(user),
                        notice.createdAt.goe(LocalDateTime.now().minusDays(7)),
                        notice.deletedAt.isNull()
                )
                .orderBy(notice.noticeId.desc())
                .fetch();

        List<GetNoticeListResponseDto> result = new ArrayList<>();

        for (Notice n : notices) {
            GetNoticeListResponseDto dto = GetNoticeListResponseDto.builder()
                    .noticeId(n.getNoticeId())
                    .type(n.getType())
                    .origin(n.getOrigin())
                    .content(n.getContent())
                    .writer(n.getWriter())
                    .relativeTime(ChronoUtil.formatDateTime(n.getCreatedAt()))
                    .pageId(n.getOriginId())
                    .commentId(n.getCommentId())
                    .hasMore(false)
                    .build();

            result.add(dto);
        }

        return result;
    }

    private BooleanExpression noticeTypeCondition() {
        return notice.type.in("commerce_success", "commerce_fail", "comment", "reply", "chat");
    }

    private BooleanExpression baseNoticeCondition(User user) {
        return notice.user.eq(user)
                .and(notice.createdAt.goe(LocalDateTime.now().minusDays(7)))
                .and(notice.deletedAt.isNull());
    }
}
