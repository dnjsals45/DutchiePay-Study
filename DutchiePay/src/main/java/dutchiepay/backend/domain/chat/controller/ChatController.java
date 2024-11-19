package dutchiepay.backend.domain.chat.controller;

import dutchiepay.backend.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatroomService;

    @PostMapping("/create/room")
    public ResponseEntity<?> createRoom() {
        chatroomService.createChatRoom();
        return ResponseEntity.ok().body("Room created");
    }
}
