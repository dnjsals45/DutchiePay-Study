package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Table(name = "BuyPost")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuyPost extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buyPostId;

    private Long productId;

    // 제목
    @Column(nullable = false, length = 50)
    private String title;

    // 공구 마감날짜
    @Column(nullable = false)
    private LocalDate deadline;

    // 최소 충족 수량
    @Column(nullable = false)
    private Integer skeleton;

    // 현재 수량
    @Column(nullable = false)
    private Integer nowCount;

    // 카테고리
    @Column(nullable = false)
    private Integer category;
}
