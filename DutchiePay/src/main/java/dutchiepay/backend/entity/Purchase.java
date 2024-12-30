package dutchiepay.backend.entity;

import dutchiepay.backend.domain.community.dto.UpdatePurchaseRequestDto;
import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Purchase")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    //작성자 Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //게시글(상품)명
    @Column(nullable = false, length = 60)
    private String title;

    //내용
    @Column(nullable = false, length = 3000)
    private String contents;

    //가격
    @Column(nullable = false)
    private int price;

    //썸네일
    @Column(length = 500)
    private String thumbnail;

    //지역
    @Column(nullable = false, length = 15)
    private String location;

    // 위도
    @Column(columnDefinition = "TEXT", nullable = false)
    private String latitude;

    // 경도
    @Column(columnDefinition = "TEXT", nullable = false)
    private String longitude;

    //상태
    @Column(nullable = false, length = 10)
    private String state;

    //조회수
    @Column(nullable = false)
    private int hits;

    //약속 장소
    @Column(nullable = false, length = 50)
    private String meetingPlace;

    //상품
    @Column(nullable = false)
    private String goods;

    @Column(nullable = false)
    private String category;

    @Column(length = 1500)
    private String images;

    public void updatePurchase(UpdatePurchaseRequestDto updateDto, String images) {
        this.title = updateDto.getTitle();
        this.contents = updateDto.getContent();
        this.price = updateDto.getPrice();
        this.category = updateDto.getCategory();
        this.images = images;
        this.thumbnail = updateDto.getThumbnail();
        this.goods = updateDto.getGoods();
        this.latitude = updateDto.getLatitude();
        this.longitude = updateDto.getLongitude();
        this.meetingPlace = updateDto.getMeetingPlace();
    }

    public void changeState(String state) {
        this.state = state;
    }

    public void increaseHitCount() {this.hits++;}
}