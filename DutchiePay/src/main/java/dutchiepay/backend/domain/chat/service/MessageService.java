package dutchiepay.backend.domain.chat.service;

import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.domain.chat.repository.MessageRepository;
import dutchiepay.backend.entity.ChatRoom;
import dutchiepay.backend.entity.Message;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UserChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;

    public void enterChatRoom(User user, ChatRoom chatRoom) {
        Message enterMessage = Message.builder()
                .chatroom(chatRoom)
                .type("enter")
                .senderId(user.getUserId())
                .content(user.getNickname() + "님이 입장하셨습니다.")
                .date(LocalDate.now().toString())
                .time(LocalTime.now().toString())
                .build();

        messageRepository.save(enterMessage);

        simpMessagingTemplate.convertAndSend("/sub?chatRoomId=" + chatRoom.getChatroomId(), MessageResponse.of(enterMessage));
    }

    public void leaveChatRoom(UserChatRoom ucr) {
        Message leaveMessage = Message.builder()
                .chatroom(ucr.getChatroom())
                .type("out")
                .senderId(ucr.getUser().getUserId())
                .content(ucr.getUser().getNickname() + "님이 퇴장하셨습니다.")
                .date(LocalDate.now().toString())
                .time(LocalTime.now().toString())
                .build();

        messageRepository.save(leaveMessage);

        simpMessagingTemplate.convertAndSend("/sub?chatRoomId=" + ucr.getChatroom().getChatroomId(), MessageResponse.of(leaveMessage));
    }

    public void kickedChatRoom(UserChatRoom target) {
        Message kickedMessage = Message.builder()
                .chatroom(target.getChatroom())
                .type("ban")
                .senderId(target.getUser().getUserId())
                .content(target.getUser().getNickname() + "님이 강퇴당하셨습니다.")
                .date(LocalDate.now().toString())
                .time(LocalTime.now().toString())
                .build();

        messageRepository.save(kickedMessage);

        simpMessagingTemplate.convertAndSend("/sub?chatRoomId=" + target.getChatroom().getChatroomId(), MessageResponse.of(kickedMessage));
    }
}
