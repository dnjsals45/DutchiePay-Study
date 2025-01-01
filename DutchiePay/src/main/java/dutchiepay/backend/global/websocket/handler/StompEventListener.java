package dutchiepay.backend.global.websocket.handler;

import dutchiepay.backend.domain.chat.repository.MessageRepository;
import dutchiepay.backend.domain.chat.repository.UserChatroomRepository;
import dutchiepay.backend.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompEventListener extends DefaultHandshakeHandler {
    private final UserChatroomRepository userChatroomRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;

    /**
     * 구독 이벤트 핸들러
     * @param event
     */
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();

        String userId = accessor.getSessionAttributes().get("userId").toString();
        Long chatRoomId = extractChatRoomId(destination);

        chatRoomService.sendChatRoomInfo(userId, chatRoomId);
    }

    @EventListener
    public void sessionConnectEvent(SessionConnectedEvent event) {
    }

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent event) {
    }

    private Long extractChatRoomId(String destination) {
        String[] parts = destination.split("\\?");
        if (parts.length > 1) {
            String[] params = parts[1].split("=");
            if (params.length > 1 && "chatRoomId".equals(params[0])) {
                return Long.parseLong(params[1]);
            }
        }
        throw new IllegalArgumentException("Invalid subscription URL format");
    }
}
