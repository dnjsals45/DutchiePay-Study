package dutchiepay.backend.domain.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dutchiepay.backend.entity.Ask;
import dutchiepay.backend.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BuyAskResponseDto {
    private Long askId;
    private Long userId;
    private String nickname;
    private String content;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
    @JsonProperty("isSecret")
    private boolean secret;

    public static BuyAskResponseDto toDto(Ask ask) {
        return BuyAskResponseDto.builder()
                .askId(ask.getAskId())
                .userId(ask.getUser().getUserId())
                .nickname(ask.getUser().getNickname())
                .content(ask.getContents())
                .answer(ask.getAnswer())
                .createdAt(ask.getCreatedAt())
                .answeredAt(ask.getAnsweredAt())
                .secret(ask.isSecret())
                .build();
    }
}
