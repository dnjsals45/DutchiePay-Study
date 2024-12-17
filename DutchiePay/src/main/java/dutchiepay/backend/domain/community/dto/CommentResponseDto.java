package dutchiepay.backend.domain.community.dto;
import com.querydsl.core.Tuple;
import dutchiepay.backend.entity.Comment;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommentResponseDto {
    private List<CommentDetail> comments;
    private Long cursor;

    @Getter
    @NoArgsConstructor
    public static class CommentDetail {
        private Long commentId;
        private String nickname;
        private String profileImg;
        private String contents;
        private LocalDateTime createdAt;
        private Boolean isModified;
        private String userState;
        private Boolean hasMore;

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }

    }
    public static CommentResponseDto toDto(List<CommentDetail> comments, Long cursor) {
        return CommentResponseDto.builder().comments(comments).cursor(cursor).build();
    }
}