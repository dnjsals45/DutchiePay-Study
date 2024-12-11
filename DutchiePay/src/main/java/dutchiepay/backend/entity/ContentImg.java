package dutchiepay.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "ContentImg")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentImgId;

    @ManyToOne
    @JoinColumn(name = "freeId")
    private Free free;

    @Column(length = 500)
    private String url;

}

