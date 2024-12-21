package dutchiepay.backend.domain.community.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PurchaseListResponseDto {

    private List<PurchaseDetail> posts;
    private Long cursor;

    @Getter
    @AllArgsConstructor
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
        private LocalDateTime createdAt;
        private String category;
    }
}
