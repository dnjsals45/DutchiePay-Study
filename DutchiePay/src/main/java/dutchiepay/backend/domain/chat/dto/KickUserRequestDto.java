package dutchiepay.backend.domain.chat.dto;

import lombok.Getter;

@Getter
public class KickUserRequestDto {
    private Long chatRoomId;
    private Long userId;
}
