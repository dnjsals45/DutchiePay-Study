package dutchiepay.backend.domain.chat.dto;

import dutchiepay.backend.entity.UserChatRoom;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetChatRoomListResponseDto {
    private Long chatRoomId;
    private String chatRoomName;

    public static List<GetChatRoomListResponseDto> from(List<UserChatRoom> allByUser) {
        List<GetChatRoomListResponseDto> result = new ArrayList<>();

        for (UserChatRoom userChatRoom : allByUser) {
            result.add(GetChatRoomListResponseDto.builder()
                    .chatRoomId(userChatRoom.getChatroom().getChatroomId())
                    .chatRoomName(userChatRoom.getChatroom().getChatRoomName())
                    .build());
        }

        return result;
    }
}
