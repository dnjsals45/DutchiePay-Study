package dutchiepay.backend.domain.community.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMartResponseDto {
    private Long writerId;
    private String writer;
    private String writerProfileImage;
    private String title;
    private String category;
    private String content;
    private String meetingPlace;
    private String longitude;
    private String latitude;
    private String state;
    private LocalDateTime createdAt;
    private String date;
    private Integer maximum;
    private Integer now;
    private Integer hits;
}
