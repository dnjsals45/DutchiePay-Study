package dutchiepay.backend.domain.community.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMartListResponseDto {
    private List<MartDto> posts;
    private Long cursor;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MartDto {
        private Long shareId;
        private String category;
        private String writer;
        private String writerProfileImg;
        private String thumbnail;
        private String title;
        private String meetingPlace;
        private String state;
        private String relativeTime;
        private String date;
        private Integer maximum;
        private Integer now;
    }
}
