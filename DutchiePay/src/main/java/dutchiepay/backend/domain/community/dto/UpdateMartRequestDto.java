package dutchiepay.backend.domain.community.dto;

import jakarta.validation.constraints.Max;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMartRequestDto {
    private Long shareId;
    private String title;
    private String date;
    @Max(value = 10, message = "최대 인원은 10명을 넘을 수 없습니다.")
    private Integer maximum;
    private String meetingPlace;
    private String latitude;
    private String longitude;
    private String content;
    private String thumbnail;
    private String category;
}
