package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Notice")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String type;

    private String origin;

    private String content;

    private Long originId;

    private Long commentId;

    private String writer;

    private Boolean isRead;

    public void read() {
        this.isRead = true;
    }
}
