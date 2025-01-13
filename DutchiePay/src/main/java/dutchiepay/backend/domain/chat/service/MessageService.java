package dutchiepay.backend.domain.chat.service;

import dutchiepay.backend.domain.chat.dto.MessageResponse;
import dutchiepay.backend.domain.chat.repository.MessageRepository;
import dutchiepay.backend.entity.ChatRoom;
import dutchiepay.backend.entity.Message;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UserChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private static final String CHAT_ROOM_PREFIX = "/sub/chat/";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;
    private final RedisMessageService redisMessageService;

    public void enterChatRoom(User user, ChatRoom chatRoom) {
        Message enterMessage = Message.builder()
                .chatroom(chatRoom)
                .type("enter")
                .senderId(user.getUserId())
                .content(user.getNickname() + "님이 입장하셨습니다.")
                .date(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .time(LocalTime.now().format(DateTimeFormatter.ofPattern("a h:m").withLocale(Locale.KOREA)))
                .build();

        enterMessage = messageRepository.save(enterMessage);
        redisMessageService.saveMessage(String.valueOf(chatRoom.getChatroomId()), enterMessage);

        simpMessagingTemplate.convertAndSend(CHAT_ROOM_PREFIX + chatRoom.getChatroomId(), MessageResponse.of(enterMessage));
    }

    public void leaveChatRoom(UserChatRoom ucr) {
        Message leaveMessage = Message.builder()
                .chatroom(ucr.getChatroom())
                .type("out")
                .senderId(ucr.getUser().getUserId())
                .content(ucr.getUser().getNickname() + "님이 퇴장하셨습니다.")
                .date(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .time(LocalTime.now().format(DateTimeFormatter.ofPattern("a h:m").withLocale(Locale.KOREA)))
                .build();

        leaveMessage = messageRepository.save(leaveMessage);
        redisMessageService.saveMessage(String.valueOf(ucr.getChatroom().getChatroomId()), leaveMessage);

        simpMessagingTemplate.convertAndSend(CHAT_ROOM_PREFIX + ucr.getChatroom().getChatroomId(), MessageResponse.of(leaveMessage));
    }

    public void kickedChatRoom(UserChatRoom target) {
        Message kickedMessage = Message.builder()
                .chatroom(target.getChatroom())
                .type("ban")
                .senderId(target.getUser().getUserId())
                .content(target.getUser().getNickname() + "님이 강퇴당하셨습니다.")
                .date(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .time(LocalTime.now().format(DateTimeFormatter.ofPattern("a h:m").withLocale(Locale.KOREA)))
                .build();

        kickedMessage = messageRepository.save(kickedMessage);
        redisMessageService.saveMessage(String.valueOf(target.getChatroom().getChatroomId()), kickedMessage);

        simpMessagingTemplate.convertAndSend(CHAT_ROOM_PREFIX + target.getChatroom().getChatroomId(), MessageResponse.of(kickedMessage));
    }
}
