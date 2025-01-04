package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.domain.chat.dto.GetMessageListResponseDto;

import java.util.List;

public interface QChatRoomRepository {
    List<GetMessageListResponseDto> findChatRoomMessages(Long chatRoomId);
}
