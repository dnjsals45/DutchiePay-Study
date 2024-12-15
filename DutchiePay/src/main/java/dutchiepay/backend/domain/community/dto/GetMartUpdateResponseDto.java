package dutchiepay.backend.domain.community.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMartUpdateResponseDto {
    private String title;
    private String category;
    private String content;
    private String meetingPlace;
    private String longitude;
    private String latitude;
    private String date;
    private Integer maximum;
    private String thumbnail;
    private String[] images;
}
