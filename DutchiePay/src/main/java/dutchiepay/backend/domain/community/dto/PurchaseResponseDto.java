package dutchiepay.backend.domain.community.dto;

import dutchiepay.backend.entity.Purchase;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PurchaseResponseDto {
    private Long writerId;
    private String writer;
    private String writerProfileImage;
    private String title;
    private String category;
    private String contents;
    private String goods;
    private Integer price;
    private String meetingPlace;
    private String latitude;
    private String longitude;
    private String state;
    private LocalDateTime createdAt;
    private Integer hits;

    public static PurchaseResponseDto toDto(Purchase purchase) {
        return PurchaseResponseDto.builder()
                .writerId(purchase.getUser().getUserId())
                .writer(purchase.getUser().getNickname())
                .writerProfileImage(purchase.getUser().getProfileImg())
                .title(purchase.getTitle())
                .category(purchase.getPrice() == -1? "share" : "trade")
                .contents(purchase.getContents())
                .goods(purchase.getGoods())
                .price(purchase.getPrice())
                .meetingPlace(purchase.getMeetingPlace())
                .latitude(purchase.getLatitude())
                .longitude(purchase.getLongitude())
                .state(purchase.getState())
                .createdAt(purchase.getCreatedAt())
                .hits(purchase.getHits())
                .build();
    }
}
