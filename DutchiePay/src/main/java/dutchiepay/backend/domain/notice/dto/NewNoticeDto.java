package dutchiepay.backend.domain.notice.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewNoticeDto {
    private String message;
}
