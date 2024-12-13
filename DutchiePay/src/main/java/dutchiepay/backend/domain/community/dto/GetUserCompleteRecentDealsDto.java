package dutchiepay.backend.domain.community.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUserCompleteRecentDealsDto {
    private Long postId;
    private String category;
    private String title;
    private LocalDateTime createdAt;
}
