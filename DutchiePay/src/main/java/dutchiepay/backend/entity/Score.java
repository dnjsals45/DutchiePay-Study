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
    @JoinColumn(name = "buy_id")
    private Buy buy;

    //1점 개수, 0부터 시작
    @Column(nullable = false)
    private int one;

    //2점 개수
    @Column(nullable = false)
    private int two;

    //3점 개수
    @Column(nullable = false)
    private int three;

    //4점 개수
    @Column(nullable = false)
    private int four;

    //5점 개수
    @Column(nullable = false)
    private int five;

    //후기 개수
    @Column(nullable = false)
    private int count;

}