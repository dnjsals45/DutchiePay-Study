package dutchiepay.backend.domain.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMessageListResponseDto {
    private List<MessageResponse> messages;
    private String cursor;
}
