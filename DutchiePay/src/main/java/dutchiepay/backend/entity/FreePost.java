package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "FreePost")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreePost extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long freePostId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 60, nullable = false)
    private String title;

    @Column(length = 3000, nullable = false)
    private String contents;

    @Column(length = 10, nullable = false)
    private String category;

    @Column(length = 500)
    private String postImg;

    @Column(nullable = false)
    private Long hits;

}
