package dutchiepay.backend.domain.chat.controller;

import dutchiepay.backend.domain.chat.dto.ChatMessage;
import dutchiepay.backend.domain.chat.dto.JoinChatRoomRequestDto;
import dutchiepay.backend.domain.chat.dto.KickUserRequestDto;
import dutchiepay.backend.domain.chat.service.ChatRoomService;
import dutchiepay.backend.domain.chat.service.RedisMessageService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatroomService;
    private final RedisMessageService redisMessageService;

    @MessageMapping("/chat/{chatRoomId}")
    public ChatMessage chat(@DestinationVariable String chatRoomId, ChatMessage message) {
        chatroomService.sendToChatRoomUser(chatRoomId, message);
        return message;
    }

    @Operation(summary = "채팅방 입장", description = "postId에 연결된 채팅방 입장")
    @PostMapping("/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinChatRoomFromPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestBody JoinChatRoomRequestDto dto) {
        return ResponseEntity.ok().body(chatroomService.joinChatRoomFromPost(userDetails.getUser(), dto));
    }

    @Operation(summary = "채팅방 나가기")
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> leaveChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestParam Long chatRoomId) {
        chatroomService.leaveChatRoom(userDetails.getUser(), chatRoomId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 목록 조회")
    @GetMapping("/chatRoomList")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getChatRoomList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(chatroomService.getChatRoomList(userDetails.getUser()));
    }

    @Operation(summary = "사용자 내보내기")
    @PostMapping("/kick")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> kickUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @RequestBody KickUserRequestDto dto) {
        chatroomService.kickUser(userDetails.getUser(), dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅 사용자 목록 조회")
    @GetMapping("/users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getChatRoomUsers(@RequestParam Long chatRoomId) {
        return ResponseEntity.ok(chatroomService.getChatRoomUsers(chatRoomId));
    }

    @Operation(summary = "채팅방 메시지 목록 조회")
    @GetMapping("/message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getChatRoomMessages(@RequestParam(value = "chatRoomId") Long chatRoomId,
                                                 @RequestParam(value = "cursor", required = false) String cursor,
                                                 @RequestParam(value = "limit") Long limit) {
        return ResponseEntity.ok(chatroomService.getChatRoomMessages(chatRoomId, cursor, limit));
    }

    @Operation(summary = "method 테스트")
    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> test() {
        chatroomService.sendChatRoomInfo("2", 14L);
        return ResponseEntity.ok().build();
    }
}
