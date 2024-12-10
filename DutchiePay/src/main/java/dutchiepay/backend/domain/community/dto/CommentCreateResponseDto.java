package dutchiepay.backend.domain.community.dto;

import dutchiepay.backend.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentCreateResponseDto {
    private Long commentId;
    private LocalDateTime createdAt;

    public static CommentCreateResponseDto toDto(Comment comment) {
        return CommentCreateResponseDto.builder()
                .commentId(comment.getCommentId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
