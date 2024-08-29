package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "Ask")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ask extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long askId;

    //작성자 ID
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    //공구게시글 ID
    @ManyToOne
    @JoinColumn(name = "buyPostId")
    private BuyPost buyPost;

    //상품 ID
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    //주문번호
    @Column
    private Long orderNum;

    //문의내용
    @Column(nullable = false, length = 1000)
    private String contents;

    //비공개 여부 true = 비공개
    @Column(nullable = false)
    private boolean secret;

    //답변
    @Column(length = 255)
    private String answer;

    //답변 날짜
    @Column
    private LocalDateTime answeredAt;

}