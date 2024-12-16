package dutchiepay.backend.domain.chat.service;

import dutchiepay.backend.domain.chat.dto.ChatMessage;
import dutchiepay.backend.domain.chat.dto.CursorResponse;
import dutchiepay.backend.domain.chat.dto.GetChatRoomListResponseDto;
import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.domain.chat.repository.ChatRoomRepository;
import dutchiepay.backend.domain.chat.repository.MessageRepository;
import dutchiepay.backend.domain.chat.repository.UserChatroomRepository;
import dutchiepay.backend.entity.ChatRoom;
import dutchiepay.backend.entity.Message;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UserChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserChatroomRepository userChatroomRepository;

    @Transactional
    public void createChatRoom() {
        ChatRoom newChat = ChatRoom.builder()
                .postId(4L)
                .maxPartInc(100)
                .nowPartInc(0)
                .build();

        chatRoomRepository.save(newChat);
    }

    @Transactional
    public void joinChatRoom(User user, String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatRoomId))
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        if (chatRoom.getNowPartInc() >= chatRoom.getMaxPartInc()) {
            throw new IllegalArgumentException("채팅방이 꽉 찼습니다.");
        }

        chatRoom.joinUser();

        UserChatRoom userChatRoom = UserChatRoom.builder()
                .chatroom(chatRoom)
                .user(user)
                .build();

        userChatroomRepository.save(userChatRoom);
    }

    @Transactional
    public void sendToChatRoomUser(String chatRoomId, ChatMessage message) {
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatRoomId))
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        Message newMessage = Message.builder()
                .chatroom(chatRoom)
                .senderId(message.getSender())
                .content(message.getContent())
                .unreadCount(chatRoom.getNowPartInc() - getSubscribedUserCount(chatRoomId))
                .build();

        messageRepository.save(newMessage);

        updateLastMessageToAllSubscribers(chatRoomId, newMessage.getMessageId());

        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, MessageResponse.of(newMessage));
    }

    public List<MessageResponse> getChatRoomMessageList(User user, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        List<Message> messageList = messageRepository.findAllByChatroom(chatRoom);
        List<MessageResponse> dto = new ArrayList<>();

        for (Message message : messageList) {
            dto.add(MessageResponse.of(message));
        }

        return dto;
    }

    public void checkCursorId(Long chatRoomId, Long userId) {
        Long cursor = messageRepository.findCursorId(chatRoomId, userId);

        if (cursor != null) {
            simpMessagingTemplate.convertAndSend("/sub/chat/room/read/" + chatRoomId, CursorResponse.of(cursor));
            userChatroomRepository.updateLastMessageToUser(userId, chatRoomId);
        } else {
            simpMessagingTemplate.convertAndSend("/sub/chat/room/read/" + chatRoomId, CursorResponse.of(0L));
            userChatroomRepository.updateLastMessageToUser(userId, chatRoomId);
        }
    }

    private void updateLastMessageToAllSubscribers(String chatRoomId, Long messageId) {
        List<Long> userIds = new ArrayList<>();

        for (SimpUser user : simpUserRegistry.getUsers()) {
            for (SimpSession session : user.getSessions()) {
                for (SimpSubscription subscription : session.getSubscriptions()) {
                    if (subscription.getDestination().equals("/sub/chat/room/" + chatRoomId)) {
                        userIds.add(Long.parseLong(user.getName()));
                    }
                }
            }
        }

        userChatroomRepository.updateLastMessageToAllSubscribers(userIds, Long.parseLong(chatRoomId), messageId);
    }

    private int getSubscribedUserCount(String chatRoomId) {
        String destination = "/sub/chat/room/" + chatRoomId;
        int count = 0;

        for (SimpUser user : simpUserRegistry.getUsers()) {
            for (SimpSession session : user.getSessions()) {
                for (SimpSubscription subscription : session.getSubscriptions()) {
                    if (destination.equals(subscription.getDestination())) {
                        count++;
                        break;
                    }
                }
            }
        }

        return count;
    }

    public List<GetChatRoomListResponseDto> getChatRoomList(User user) {
        return GetChatRoomListResponseDto.from(userChatroomRepository.findAllByUser(user));
    }

    public void sendListUnreadMessage(String userId) {
        List<UserChatRoom> userChatRoomList = userChatroomRepository.findAllByUserId(Long.valueOf(userId));

        String destination = "/chat/list/message";

        simpMessagingTemplate.convertAndSendToUser(
                userId,
                destination,
                "hello");
    }
}
