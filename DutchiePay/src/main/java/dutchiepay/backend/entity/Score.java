package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Score")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Score extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;

    //공구게시글 ID
    @OneToOne
    @JoinColumn(name = "buy_post_id")
    private BuyPost buyPost;

    //1점 개수, 0부터 시작
    @Column(nullable = false)
    private Integer one;

    //2점 개수
    @Column(nullable = false)
    private Integer two;

    //3점 개수
    @Column(nullable = false)
    private Integer three;

    //4점 개수
    @Column(nullable = false)
    private Integer four;

    //5점 개수
    @Column(nullable = false)
    private Integer five;

    //후기 개수
    @Column(nullable = false)
    private Integer count;

}