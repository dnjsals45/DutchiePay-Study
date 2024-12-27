package dutchiepay.backend.domain.notice.dto;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetNoticeListResponseDto {
    private Long noticeId;
    private String type;
    private String origin;
    private String content;
    private String relativeTime;
    private String writer;
    private Long pageId;
    private Long commentId;
    private Boolean hasMore;
    private Long count;
}
