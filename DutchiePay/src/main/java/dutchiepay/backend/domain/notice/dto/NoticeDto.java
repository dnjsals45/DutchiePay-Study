package dutchiepay.backend.domain.notice.dto;

import dutchiepay.backend.domain.ChronoUtil;
import dutchiepay.backend.entity.Notice;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDto {
    private String type;
    private String writer;
    private String relativeTime;
    private Long id;

    public static NoticeDto toDto(Notice notice) {
        return NoticeDto.builder()
                .type(notice.getType())
                .writer(notice.getWriter())
                .relativeTime(ChronoUtil.timesAgo(notice.getCreatedAt()))
                .id(notice.getOriginId())
                .build();
    }

    public static List<NoticeDto> toDtoList(List<Notice> notices) {
        List<NoticeDto> noticeDtoList = new ArrayList<>();

        for (Notice n : notices) {
            noticeDtoList.add(toDto(n));
        }

        return noticeDtoList;
    }
}
