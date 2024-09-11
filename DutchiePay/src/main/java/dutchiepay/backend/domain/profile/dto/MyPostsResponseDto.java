package dutchiepay.backend.domain.profile.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPostsResponseDto {
    private Long postId;
    private String title;
    private String writeTime;
    private String content;
    private String category;
    private Long commentCount;
    private String thumbnail;
}
