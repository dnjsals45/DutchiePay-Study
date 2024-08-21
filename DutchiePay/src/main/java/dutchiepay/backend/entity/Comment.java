package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "Comment")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private Long freePostId;

    private Long userId;

    // 상위 댓글 Id
    private Long parentId;

    // 언급한 댓글 Id
    private Long mentionedId;

    // 댓글 내용
    @Column(nullable = false, length = 800)
    private String contents;
}
