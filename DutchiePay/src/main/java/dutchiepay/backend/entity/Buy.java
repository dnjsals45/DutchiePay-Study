package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Table(name = "Buy")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Buy extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buyId;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 제목
    @Column(nullable = false, length = 50)
    private String title;

    // 공구 마감날짜
    @Column(nullable = false)
    private LocalDate deadline;

    // 최소 충족 수량
    @Column(nullable = false)
    private int skeleton;

    // 현재 수량
    @Column(nullable = false)
    private int nowCount;
}
