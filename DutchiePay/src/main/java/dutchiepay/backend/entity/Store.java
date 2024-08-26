package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "Store")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long storeId;

    @Column(length = 50, nullable = false)
    private String storeName;

    @Column(length = 11, nullable = false)
    private String contactNumber;

    @Column(length = 30, nullable = false)
    private String representative;

    @Column(nullable = false)
    private String storeAddress;

    @Column
    private LocalDateTime deletedAt;
}
