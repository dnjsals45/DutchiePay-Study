package dutchiepay.backend.domain.community.dto;

import com.querydsl.core.Tuple;
import dutchiepay.backend.entity.Comment;
import dutchiepay.backend.entity.QComment;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static dutchiepay.backend.entity.QComment.comment;

@Getter
@Builder
public class CommentResponseDto {
    private List<CommentDetail> comments;
    private Long cursor;

    @Getter
    @Builder
    public static class CommentDetail {
        private Long commentId;
        private String nickname;
        private String profileImg;
        private String contents;
        private LocalDateTime createdAt;
        private Boolean isModified;
        private String userState;
        private Boolean hasMore;

        public static CommentDetail toDto(Tuple tuple, boolean hasMore) {
            boolean leaved = tuple.get(comment.user.state) != 0;
            boolean deleted = tuple.get(comment.deletedAt) != null;

            return CommentDetail.builder().commentId(tuple.get(comment.commentId))
                    .nickname(deleted || leaved ? null : tuple.get(comment.user.nickname))
                    .profileImg(deleted || leaved ? null : tuple.get(comment.user.profileImg))
                    .contents(deleted ? "삭제된 댓글입니다." : tuple.get(comment.contents))
                    .createdAt(tuple.get(comment.createdAt))
                    .isModified(deleted || leaved ? null : tuple.get(comment.updatedAt).isAfter(tuple.get(comment.createdAt)))
                    .userState(leaved ? "탈퇴" : "회원")
                    .hasMore(hasMore).build();
        }

    }

    public static CommentResponseDto toDto(List<CommentDetail> comments, Long cursor) {
        return CommentResponseDto.builder().comments(comments).cursor(cursor).build();
    }
}