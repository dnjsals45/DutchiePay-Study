package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "Purchase")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase extends Auditing {

    @Id
    @GenerateValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    //작성자 Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users users;

    //게시글(상품)명
    @Column(nullable = false, length = 60)
    private String title;

    //내용
    @Column(nullable = false, length = 3000)
    private String contents;

    //가격
    @Column(nullable = false)
    private Integer price;

    //썸네일
    @Column(length = 500)
    private String thumbnail;

    //지역
    @Column(nullable = false, length = 15)
    private String location;

    //상태
    @Column(nullable = false, length = 10)
    private String state;

    //조회수
    @Column(nullable = false)
    private Integer hits;

    //약속 장소
    @Column(nullable = false, length = 50)
    private String meetingPlace;

}