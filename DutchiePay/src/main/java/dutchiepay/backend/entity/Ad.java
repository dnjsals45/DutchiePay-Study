package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "Ad")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ad extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adId;

    private Long buyPostId;

    // 만료 시간
    @Column(nullable = false)
    private LocalDateTime expireDate;
}
