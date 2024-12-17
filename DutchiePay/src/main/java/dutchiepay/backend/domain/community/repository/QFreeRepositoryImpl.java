package dutchiepay.backend.domain.community.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dutchiepay.backend.domain.ChronoUtil;
import dutchiepay.backend.domain.community.dto.*;
import dutchiepay.backend.domain.community.exception.CommunityErrorCode;
import dutchiepay.backend.domain.community.exception.CommunityException;
import dutchiepay.backend.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QFreeRepositoryImpl implements QFreeRepository {

    private final JPAQueryFactory jpaQueryFactory;

    QFree free = QFree.free;
    QComment comment = QComment.comment;

    @Override
    public FreeListResponseDto getFreeLists(String category, String filter, String word, int limit, Long cursor) {
        // 커서 초기화
        if (cursor == null) cursor = Long.MAX_VALUE;

        // 기본 select, from 문에 조건 추가
        JPAQuery<Free> query = jpaQueryFactory
                .selectFrom(free)
                .leftJoin(comment).on(comment.free.eq(free))
                .groupBy(free);

        // category가 있으면 검색 조건에 category 추가
        if (StringUtils.hasText(category)) {
            query.where(free.category.eq(category));
        }

        // word가 있으면 검색 조건에 word 추가
        if (StringUtils.hasText(word)) {
            query.where(free.title.contains(word));
        }

        OrderSpecifier[] orderSpecifier;
        switch (filter) {
            case "new":
                orderSpecifier = new OrderSpecifier[]{free.createdAt.desc()};
                query.where(free.freeId.loe(cursor),
                        free.deletedAt.isNull());
                break;
            case "comment":
                orderSpecifier = new OrderSpecifier[]{comment.count().desc(), free.freeId.desc()};
                if (cursor < Long.MAX_VALUE) {
                    Long nowCommentsCount = jpaQueryFactory.
                            select(comment.count())
                            .from(comment)
                            .join(comment.free, free)
                            .where(free.freeId.eq(cursor),
                                    free.deletedAt.isNull())
                            .fetchFirst();
                    if (nowCommentsCount != null) {
                        query.having(comment.count().lt(nowCommentsCount)
                                .or(comment.count().eq(nowCommentsCount))
                                .and(free.freeId.loe(cursor)));
                    }
                }
                break;
            case "view":
                orderSpecifier = new OrderSpecifier[]{free.hits.desc(), free.freeId.desc()};
                if (cursor < Long.MAX_VALUE) {
                    Integer nowViews = jpaQueryFactory
                            .select(free.hits)
                            .from(free)
                            .where(free.freeId.eq(cursor),
                                    free.deletedAt.isNull())
                            .fetchFirst();
                    if (nowViews != null) {
                        query.where(free.hits.lt(nowViews)
                                .or(free.hits.eq(nowViews)).and(free.freeId.ne(cursor)));
                    }
                }
                break;
            default:
                throw new CommunityException(CommunityErrorCode.ILLEGAL_FILTER);
        }

        query.limit(limit + 1).orderBy(orderSpecifier);
        List<Free> posts = query.fetch();
        Long nextCursor = posts.size() > limit ? posts.get(limit).getFreeId() : null;

        return FreeListResponseDto.toDto(posts.stream().map(free ->
                        FreeListResponseDto.FreeList.toDto(free, ChronoUtil.timesAgo(free.getCreatedAt()), countComments(free)))
                .collect(Collectors.toList()), nextCursor);
    }

    @Override
    public FreePostResponseDto getFreePost(Long freeId) {
        Free result = jpaQueryFactory
                .selectFrom(free)
                .where(free.freeId.eq(freeId),
                        free.deletedAt.isNull())
                .fetchFirst();

        if (result == null) throw new CommunityException(CommunityErrorCode.CANNOT_FOUND_POST);

        return FreePostResponseDto.toDto(result.getUser(), result, countComments(result));
    }

    @Override
    public List<HotAndRecommendsResponseDto.Posts> getHotPosts() {

        return jpaQueryFactory
                .selectFrom(free)
                .where(free.createdAt.goe(LocalDateTime.now().minusDays(7)),
                        free.deletedAt.isNull())
                .orderBy(free.hits.desc())
                .limit(5)
                .fetch()
                .stream()
                .map(free -> HotAndRecommendsResponseDto.Posts.toDto(free, countComments(free)))
                .toList();
    }

    @Override
    public List<HotAndRecommendsResponseDto.Posts> getRecommendsPosts(String category) {
        return jpaQueryFactory
                .selectFrom(free)
                .where(free.category.eq(category),
                        free.deletedAt.isNull())
                .orderBy(free.createdAt.desc())
                .limit(5)
                .fetch()
                .stream()
                .map(free -> HotAndRecommendsResponseDto.Posts.toDto(free, countComments(free)))
                .toList();
    }

    private Long countComments(Free free) {
        return jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.free.eq(free),
                        comment.deletedAt.isNull())
                .fetchFirst();
    }

    @Override
    public CommentResponseDto getComments(Free free, Long cursor, int limit) {
        if (cursor == null) cursor = 0L;

        // commentId가 cursor보다 크거나 같으면서, deletedAt이 Null이거나(삭제되지 않았거나)
        // deletedAt이 Null이 아니고(삭제 되었고),
        // parentId가 null이 아닌 댓글들(답글들)의 parentId 목록 안에 있는 댓글들(답글이 있는 댓글들) 조회
        List<Tuple> comments = jpaQueryFactory
                .select(
                        comment.commentId,
                        comment.user.nickname,
                        comment.user.profileImg,
                        comment.contents,
                        comment.createdAt,
                        comment.updatedAt,
                        comment.user.state,
                        comment.deletedAt
                        )
                .from(comment)
                .where(comment.free.eq(free),
                        comment.commentId.goe(cursor),
                        comment.parentId.isNull(),
                        comment.deletedAt.isNull()
                                .or(comment.deletedAt.isNotNull()
                                        .and(comment.commentId.in(
                                                JPAExpressions
                                                        .select(comment.parentId)
                                                        .from(comment)
                                                        .where(comment.parentId.isNotNull()))
                                        )

                                )
                )
                .orderBy(comment.createdAt.asc())
                .limit(limit + 1)
                .fetch();

        Long nextCursor = comments.size() > limit ? comments.get(limit).get(comment.commentId) : null;
        return CommentResponseDto.toDto(comments.stream()
                .map(c -> CommentResponseDto.CommentDetail.toDto(c,
                        countRecomments(c.get(comment.commentId)) > 5)).toList(), nextCursor);
    }

    private Long countRecomments(Long commentId) {
        return jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.parentId.eq(commentId),
                        comment.deletedAt.isNull())
                .fetchFirst();
    }

    /**
     * commentId에 달려있는 답댓 목록 -> type에 따라 다르게
     * parentId가 commentId인 애들 다 찾고 각 애들마다 mentionedId로 다시 comment 찾기
     * deletedAt이 null인(삭제되지 않은) 댓글들만 찾음
     * @param commentId 답글이 달린 원 댓글
     * @param type 처음부터 5개인지, 6번째부터 그 이후인지
     * @return 대댓글 목록
     */
    public List<ReCommentResponseDto> getReComments(Long commentId, String type) {


        JPAQuery<Tuple> query = jpaQueryFactory
                .select(comment.commentId,
                        comment.mentionedId,
                        comment.user.nickname,
                        comment.user.profileImg,
                        comment.contents,
                        comment.createdAt,
                        comment.updatedAt,
                        comment.user.state)
                .from(comment)
                .where(comment.parentId.eq(commentId), comment.deletedAt.isNull());
        if (type.equals("first")) query.limit(5);
        else if (type.equals("rest")) query.offset(6);
        else throw new CommunityException(CommunityErrorCode.ILLEGAL_TYPE);

        List<Tuple> reComments = query.fetch();

        return reComments.stream().map(r -> ReCommentResponseDto.toDto(r, findMentionedCommentUserById(r.get(comment.mentionedId))))
                .collect(Collectors.toList());
    }

    private Tuple findMentionedCommentUserById(Long commentId) {
        return jpaQueryFactory.select(comment.user.nickname,
                        comment.user.state)
                .from(comment)
                .where(comment.commentId.eq(commentId))
                .fetchFirst();
    }
}

