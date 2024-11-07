package dutchiepay.backend.domain.profile.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyAskResponseDto {
    private Long askId;
    private String storeName;
    private Long buyId;
    private String productName;
    private String content;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
    private Boolean isSecret;
}
