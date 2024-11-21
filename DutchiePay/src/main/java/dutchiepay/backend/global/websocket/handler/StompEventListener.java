package dutchiepay.backend.global.websocket.handler;

import dutchiepay.backend.domain.chat.repository.ChatRoomRepository;
import dutchiepay.backend.domain.chat.repository.MessageRepository;
import dutchiepay.backend.domain.chat.repository.UserChatroomRepository;
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

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();
        String userId = accessor.getSessionAttributes().get("userId").toString();
        Long chatRoomId = Long.parseLong(destination.substring(destination.lastIndexOf("/") + 1));

        log.info("유저 {} 이 채팅방 {}에 접속하였습니다.", userId, destination);

        Long lastMessageId = userChatroomRepository.findLastMessageId(Long.parseLong(userId), chatRoomId);

        if (lastMessageId == null) {
            lastMessageId = 0L;
        }

        // 채팅방에 접속하면 읽지 않은 메시지들의 unreadCount 개수를 감소시킨다.
        messageRepository.discountUnreadMessageCount(lastMessageId, chatRoomId);

        // 유저 본인의 lastMessageId를 최신 메시지로 업데이트한다.
//        userChatroomRepository.updateLastMessageLatestMessageId(Long.parseLong(userId), chatRoomId);
    }

    @EventListener
    public void sessionConnectEvent(SessionConnectedEvent event) {
        AbstractSubProtocolEvent subProtocolEvent = event;

        Principal user = subProtocolEvent.getUser();

        log.info("User connected: {}", user);
    }

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent event) {
        AbstractSubProtocolEvent subProtocolEvent = event;

        Principal user = subProtocolEvent.getUser();

        log.info("User disconnected: {}", user);
    }
}
