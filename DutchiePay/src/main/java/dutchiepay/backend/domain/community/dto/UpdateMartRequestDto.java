package dutchiepay.backend.domain.community.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMartRequestDto {
    private Long shareId;
    private String title;
    private String date;
    private String meetingPlace;
    private String latitude;
    private String longitude;
    private String content;
    private String thumbnail;
    private String category;
    private String[] images;
}
