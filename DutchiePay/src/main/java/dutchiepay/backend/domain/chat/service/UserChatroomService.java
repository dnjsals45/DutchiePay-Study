package dutchiepay.backend.domain.chat.service;

import dutchiepay.backend.domain.chat.dto.GetChatRoomListResponseDto;
import dutchiepay.backend.domain.chat.dto.GetChatRoomUsersResponseDto;
import dutchiepay.backend.domain.chat.exception.ChatErrorCode;
import dutchiepay.backend.domain.chat.exception.ChatException;
import dutchiepay.backend.domain.chat.repository.UserChatroomRepository;
import dutchiepay.backend.entity.ChatRoom;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UserChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserChatroomService {
    private final UserChatroomRepository userChatroomRepository;
    private final MessageService messageService;

    @Transactional
    public ChatRoom joinChatRoom(User user, ChatRoom chatRoom, String role) {
        UserChatRoom userChatRoom = UserChatRoom.builder()
                .user(user)
                .chatroom(chatRoom)
                .role(role)
                .banned(false)
                .build();

        userChatroomRepository.save(userChatRoom);
        messageService.enterChatRoom(user, chatRoom);

        chatRoom.joinUser();

        return chatRoom;
    }

    public Boolean isBanned(Long userId, Long chatRoomId) {
        return userChatroomRepository.findByUserBanned(userId, chatRoomId);
    }

    public UserChatRoom findByUserAndChatRoomId(User user, Long chatRoomId) {
        return userChatroomRepository.findByUserAndChatroomChatroomId(user, chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.INVALID_CHAT));
    }

    public void leaveChatRoom(UserChatRoom ucr) {
        userChatroomRepository.delete(ucr);
        messageService.leaveChatRoom(ucr);
    }

    public void kickedChatRoom(UserChatRoom target) {
        target.kick();
        userChatroomRepository.save(target);
        messageService.kickedChatRoom(target);
    }

    public List<UserChatRoom> findAllByUser(User user) {
        return userChatroomRepository.findAllByUser(user);
    }

    public List<GetChatRoomListResponseDto> getChatRoomList(User user) {
        return userChatroomRepository.getChatRoomList(user);
    }

    public boolean alreadyJoined(User user, ChatRoom chatRoom) {
        return userChatroomRepository.findByUserAndChatroom(user, chatRoom) != null;
    }

    public UserChatRoom findByUserUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        return userChatroomRepository.findByUserUserIdAndChatroomChatroomId(userId, chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.INVALID_CHAT));
    }

    public List<GetChatRoomUsersResponseDto> getChatRoomUsers(Long chatRoomId) {
        return userChatroomRepository.getChatRoomUsers(chatRoomId);
    }

    public void updateLastMessageToAllSubscribers(List<Long> userIds, long l, Long messageId) {
        userChatroomRepository.updateLastMessageToAllSubscribers(userIds, l, messageId);
    }

    public void updateLastMessageToUser(Long userId, Long chatRoomId) {
        userChatroomRepository.updateLastMessageToUser(userId, chatRoomId);
    }
}
