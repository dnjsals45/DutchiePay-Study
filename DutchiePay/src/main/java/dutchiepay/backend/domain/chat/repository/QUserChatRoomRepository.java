package dutchiepay.backend.domain.chat.repository;

import dutchiepay.backend.domain.chat.dto.GetChatRoomListResponseDto;
import dutchiepay.backend.domain.chat.dto.GetChatRoomUsersResponseDto;
import dutchiepay.backend.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QUserChatRoomRepository {
    @Transactional
    void updateLastMessageLatestMessageId(long userId, Long chatRoomId);

    @Transactional
    void updateLastMessageToUser(Long userId, Long chatRoomId);

    List<GetChatRoomListResponseDto> getChatRoomList(User user);

    List<GetChatRoomUsersResponseDto> getChatRoomUsers(Long chatRoomId);

    Boolean findByUserBanned(Long userId, Long chatRoomId);
}
