package dutchiepay.backend.domain.chat.controller;

import dutchiepay.backend.domain.chat.dto.ChatMessage;
import dutchiepay.backend.domain.chat.service.ChatRoomService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatroomService;

    @MessageMapping("{chatRoomId}/message")
    public ChatMessage chat(@DestinationVariable String chatRoomId, ChatMessage message) {
        chatroomService.sendToChatRoomUser(chatRoomId, message);
        return message;
    }

    @Operation(summary = "채팅방 참여", description = "postId에 연결된 채팅방 참여")
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinChatRoomFromPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestParam Long postId,
                                          @RequestParam String type) {
        return ResponseEntity.ok().body(chatroomService.joinChatRoomFromPost(userDetails.getUser(), postId, type));
    }

    @Operation(summary = "채팅방 나가기")
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> leaveChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestParam Long chatRoomId) {
        chatroomService.leaveChatRoom(userDetails.getUser(), chatRoomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/chat/message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getChatRoomMessageList(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestParam String chatRoomId) {
        return ResponseEntity.ok(chatroomService.getChatRoomMessageList(userDetails.getUser(), Long.valueOf(chatRoomId)));
    }

    @GetMapping("/api/chat/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getChatRoomList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(chatroomService.getChatRoomList(userDetails.getUser()));
    }
}
