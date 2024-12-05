package dutchiepay.backend.domain.community.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateMartRequestDto {
    private String title;
    private String date;
    @Min(value = 2, message = "최소 인원은 2명 이상이어야 합니다.")
    @Max(value = 10, message = "최대 인원은 10명을 넘을 수 없습니다.")
    private Integer maximum;
    private String meetingPlace;
    private String latitude;
    private String longitude;
    private String content;
    private String thumbnail;
    private String category;
}
