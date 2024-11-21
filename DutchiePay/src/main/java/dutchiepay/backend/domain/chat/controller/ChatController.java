package dutchiepay.backend.domain.chat.controller;

import dutchiepay.backend.domain.chat.dto.ChatMessage;
import dutchiepay.backend.domain.chat.service.ChatRoomService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatroomService;

    @MessageMapping("/chat/{chatRoomId}/message")
    public ChatMessage chat(@DestinationVariable String chatRoomId, ChatMessage message) {
        chatroomService.sendToChatRoomUser(chatRoomId, message);
        return message;
    }

    @PostMapping("/chat/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestBody String chatRoomId) {
        chatroomService.joinChatRoom(userDetails.getUser(), chatRoomId);
        return ResponseEntity.ok().build();
    }
}
