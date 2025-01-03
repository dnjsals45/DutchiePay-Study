package dutchiepay.backend.domain.chat.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinChatRoomResponseDto {
    private Long chatRoomId;

    public static JoinChatRoomResponseDto of(Long chatRoomId) {
        return JoinChatRoomResponseDto.builder()
                .chatRoomId(chatRoomId)
                .build();
    }
}
