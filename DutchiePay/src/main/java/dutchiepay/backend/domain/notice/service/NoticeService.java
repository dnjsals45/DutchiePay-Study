package dutchiepay.backend.domain.notice.service;

import dutchiepay.backend.domain.notice.dto.NoticeDto;
import dutchiepay.backend.domain.notice.repository.NoticeRepository;
import dutchiepay.backend.entity.Notice;
import dutchiepay.backend.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class NoticeService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final NoticeRepository noticeRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    public SseEmitter subscribe(User user) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitters.put(user.getUserId(), sseEmitter);
        sseEmitter.onCompletion(() -> emitters.remove(user.getUserId()));
        sseEmitter.onTimeout(() -> emitters.remove(user.getUserId()));

        sendUnreadNotification(user);

        return sseEmitter;
    }

    public void sendNotice(User user, Notice notice) {
        SseEmitter sseEmitter = emitters.get(user.getUserId());

        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("notice")
                        .data(NoticeDto.toDto(notice)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendUnreadNotification(User user) {
        List<Notice> notices = noticeRepository.findByUserAndIsReadFalseAndCreatedAtAfter(user, LocalDateTime.now().minusDays(7));
        List<NoticeDto> sendNotice = new ArrayList<>();

        for (Notice notice : notices) {
            sendNotice.add(NoticeDto.toDto(notice));
        }

        SseEmitter sseEmitter = emitters.get(user.getUserId());

        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("notice")
                        .data(sendNotice));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
