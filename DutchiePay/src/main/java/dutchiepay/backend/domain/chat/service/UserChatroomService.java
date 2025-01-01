package dutchiepay.backend.domain.chat.service;

import dutchiepay.backend.domain.chat.exception.ChatErrorCode;
import dutchiepay.backend.domain.chat.exception.ChatException;
import dutchiepay.backend.domain.chat.repository.UserChatroomRepository;
import dutchiepay.backend.entity.ChatRoom;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UserChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserChatroomService {
    private final UserChatroomRepository userChatroomRepository;

    @Transactional
    public void joinChatRoom(User user, ChatRoom chatRoom, String role) {
        UserChatRoom userChatRoom = UserChatRoom.builder()
                .user(user)
                .chatroom(chatRoom)
                .role(role)
                .banned(false)
                .build();

        userChatroomRepository.save(userChatRoom);

        chatRoom.joinUser();
    }

    public boolean isBanned(User user, ChatRoom chatRoom) {
        UserChatRoom userChatRoom = userChatroomRepository.findByUserAndChatroom(user, chatRoom);

        return userChatRoom != null && userChatRoom.getBanned();
    }

    public UserChatRoom findByUserAndChatRoomId(User user, Long chatRoomId) {
        return userChatroomRepository.findByUserAndChatroomChatroomId(user, chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.INVALID_CHAT));
    }

    public void leaveChatRoom(UserChatRoom ucr) {
        userChatroomRepository.delete(ucr);
    }
}
