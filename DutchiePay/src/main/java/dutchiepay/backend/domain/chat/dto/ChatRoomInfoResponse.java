package dutchiepay.backend.domain.chat.dto;

import dutchiepay.backend.entity.ChatRoom;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomInfoResponse {
    private String type;
    private Long userId;
    private ChatRoomInfo data;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatRoomInfo {
        private Long chatRoomId;
        private String chatRoomName;
        private String chatRoomImg;
        private Boolean isSendActivated;

        public static ChatRoomInfo from(ChatRoom chatRoom, Boolean isSendActivated) {
            return ChatRoomInfo.builder()
                    .chatRoomId(chatRoom.getChatroomId())
                    .chatRoomName(chatRoom.getChatRoomName())
                    .chatRoomImg(chatRoom.getChatRoomImg())
                    .isSendActivated(isSendActivated)
                    .build();
        }
    }

    public static ChatRoomInfoResponse from(Long userId, ChatRoom chatRoom, Boolean isSendActivated) {
        return ChatRoomInfoResponse.builder()
                .type("info")
                .userId(userId)
                .data(ChatRoomInfo.from(chatRoom, isSendActivated))
                .build();
    }
}
