package dutchiepay.backend.domain.chat.controller;

import dutchiepay.backend.domain.chat.dto.ChatMessage;
import dutchiepay.backend.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatroomService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/{chatRoomId}/message")
    public ChatMessage chat(@DestinationVariable String chatRoomId, ChatMessage message) {
        chatroomService.sendToChatRoomUser(chatRoomId, message);
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, message);
        log.info("chatRoomId: {}, message: {}", chatRoomId, message);
        return message;
    }

    @PostMapping
    public ResponseEntity<?> createRoom() {
        chatroomService.createChatRoom();
        return ResponseEntity.ok().build();
    }
}
