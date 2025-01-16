package dutchiepay.backend.domain.chat.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinChatRoomResponseDto {
    private Long chatRoomId;
    private String chatRoomName;
    private Integer chatRoomUser;

    public static JoinChatRoomResponseDto of(Long chatRoomId, String chatRoomName, Integer chatRoomUser) {
        return JoinChatRoomResponseDto.builder()
                .chatRoomId(chatRoomId)
                .chatRoomName(chatRoomName)
                .chatRoomUser(chatRoomUser)
                .build();
    }
}
