package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Review")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    //작성자 ID
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //공구게시글 ID
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    //내용
    @Column(nullable = false, length = 1000)
    private String contents;

    //별점
    @Column(nullable = false)
    private int rating;

    //후기 사진
    @Column(length = 500)
    private String reviewImg;

    //수정 횟수, 최대 2번 가능
    @Column(nullable = false)
    private int updateCount;

    public void update(String content, Integer rating, String reviewImg) {
        this.contents = content;
        this.rating = rating;
        this.reviewImg = reviewImg;
        this.updateCount++;
    }
}