package dutchiepay.backend.domain.chat.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMessageListResponseDto {
    private Long messageId;
    private String content;
    private String date;
    private String sendAt;
    private Long senderId;
    private String senderName;
    private String senderProfileImg;
    private String type;
}
