package dutchiepay.backend.entity;

import dutchiepay.backend.domain.community.dto.UpdateMartRequestDto;
import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Share")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Share extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shareId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 제목
    @Column(nullable = false, length = 30)
    private String title;

    // 내용
    @Column(nullable = false, length = 1000)
    private String contents;

    // 카테고리
    @Column(nullable = false, length = 10)
    private String category;

    // 지역
    @Column(nullable = false, length = 15)
    private String location;

    // 상태
    @Column(nullable = false, length = 10)
    private String state;

    // 첨부사진(썸네일)
    @Column(length = 500)
    private String thumbnail;

    // 일시
    private String date;

    // 최대 인원
    @Column(nullable = false)
    private int maximum;

    // 현재 인원
    @Column(nullable = false)
    private int now;

    // 조회수
    @Column(nullable = false)
    private int hits;

    // 위도
    @Column(columnDefinition = "TEXT", nullable = false)
    private String latitude;

    // 경도
    @Column(columnDefinition = "TEXT", nullable = false)
    private String longitude;

    // 약속 장소
    @Column(nullable = false)
    private String meetingPlace;

    public void update(UpdateMartRequestDto req) {
        this.title = req.getTitle();
        this.maximum = req.getMaximum();
        this.meetingPlace = req.getMeetingPlace();
        this.latitude = req.getLatitude();
        this.longitude = req.getLongitude();
        this.contents = req.getContent();
        this.thumbnail = req.getThumbnail();
        this.category = req.getCategory();
    }

    public void changeStatus(String status) {
        this.state = status;
    }

    public void increaseHitCount() {
        this.hits++;
    }
}
