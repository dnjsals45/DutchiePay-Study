package dutchiepay.backend.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class KickUserRequestDto {
    private Long chatRoomId;
    private List<Long> userId;
}
