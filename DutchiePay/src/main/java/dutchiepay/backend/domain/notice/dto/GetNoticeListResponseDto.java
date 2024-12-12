package dutchiepay.backend.domain.notice.dto;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetNoticeListResponseDto {
    private String type;
    private String origin;
    private String relativeTime;
    private Long id;
    private Integer count;
}
