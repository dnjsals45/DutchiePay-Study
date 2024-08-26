package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Report")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long reportId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private boolean check;

}
