package dutchiepay.backend.domain.notice.dto;

import dutchiepay.backend.entity.Notice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDto {
    private Long noticeId;
    private String type;
    private String writer;
    private String message;
    private LocalDateTime createdAt;

    public static NoticeDto toDto(Notice notice) {
        return NoticeDto.builder()
                .noticeId(notice.getNoticeId())
                .type(notice.getType())
                .writer(notice.getWriter())
                .message(notice.getMessage())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}
