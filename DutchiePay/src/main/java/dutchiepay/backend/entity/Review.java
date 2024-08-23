package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "Review")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends Auditing {

    @Id
    @GenerateValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    //작성자 ID
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users users;

    //공구게시글 ID
    @ManyToOne
    @JoinColumn(name = "buyPostId")
    private BuyPost buyPost;

    //내용
    @Column(nullable = false, length = 1000)
    private String contents;

    //별점
    @Column(nullable = false)
    private Integer rating;

    //후기 사진
    @Column(length = 500)
    private String reviewImg;

    //수정 횟수, 최대 2번 가능
    @Column(nullable = false)
    private Integer updateCount;
}