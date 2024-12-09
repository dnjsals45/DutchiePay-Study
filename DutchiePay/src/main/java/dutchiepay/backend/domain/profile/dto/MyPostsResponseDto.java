package dutchiepay.backend.domain.profile.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPostsResponseDto {
    private Integer totalPost;
    private List<Post> posts;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Post {
        private Long postId;
        private String category;
        private String title;
        private String writeTime;
        private String description;
        private Long commentCount;
        private String thumbnail;
        private String writerNickname;
        private String writerProfileImage;
    }
}
