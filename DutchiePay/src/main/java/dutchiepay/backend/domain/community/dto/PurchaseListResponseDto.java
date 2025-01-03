package dutchiepay.backend.domain.community.dto;

import com.querydsl.core.Tuple;
import dutchiepay.backend.domain.ChronoUtil;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static dutchiepay.backend.entity.QPurchase.purchase;

@Getter
@Builder
public class PurchaseListResponseDto {

    private List<PurchaseDetail> posts;
    private Long cursor;

    @Getter
    @Builder
    public static class PurchaseDetail {
        private Long purchaseId;
        private String writer;
        private String writerProfileImg;
        private String title;
        private String goods;
        private Integer price;
        private String thumbnail;
        private String meetingPlace;
        private String state;
        private String createdAt;
        private String category;
        public static PurchaseDetail toDto(Tuple result) {
            return PurchaseDetail.builder()
                    .purchaseId(result.get(purchase.purchaseId))
                    .writer(result.get(purchase.user.nickname))
                    .writerProfileImg(result.get(purchase.user.profileImg))
                    .title(result.get(purchase.title))
                    .goods(result.get(purchase.goods))
                    .price(result.get(purchase.price))
                    .thumbnail(result.get(purchase.thumbnail))
                    .meetingPlace(result.get(purchase.meetingPlace))
                    .state(result.get(purchase.state))
                    .createdAt(ChronoUtil.timesAgo(result.get(purchase.createdAt)))
                    .category(result.get(purchase.category))
                    .build();
        }
    }


}
