package dutchiepay.backend.domain.notice.service;

import dutchiepay.backend.domain.ChronoUtil;
import dutchiepay.backend.domain.notice.dto.UnreadStatus;
import dutchiepay.backend.domain.notice.dto.GetNoticeListResponseDto;
import dutchiepay.backend.domain.notice.dto.NoticeDto;
import dutchiepay.backend.entity.Comment;
import dutchiepay.backend.entity.Notice;
import dutchiepay.backend.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class NoticeService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final NoticeUtilService noticeUtilService;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public List<GetNoticeListResponseDto> getNotices(User user) {
        return noticeUtilService.findRecentNotices(user);


    }

    public SseEmitter subscribe(User user) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitters.put(user.getUserId(), sseEmitter);
        sseEmitter.onCompletion(() -> emitters.remove(user.getUserId()));
        sseEmitter.onTimeout(() -> emitters.remove(user.getUserId()));

        sendUnreadNotification(user);

        return sseEmitter;
    }

    public void sendCommentNotice(String writer, Comment comment) {
        Notice notice = noticeUtilService.createCommentNotice(writer, comment);

        if (notice != null) {
            sendNotice(notice.getUser(), notice);
        }
    }

    public void sendNotice(User user, Notice notice) {
        SseEmitter sseEmitter = emitters.get(user.getUserId());

        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event()
                        .data(NoticeDto.toDto(notice)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendUnreadNotification(User user) {
        boolean status = noticeUtilService.existUnreadNotification(user, LocalDateTime.now().minusDays(7));

        SseEmitter sseEmitter = emitters.get(user.getUserId());

        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event()
                        .data(UnreadStatus.builder()
                                .isUnread(status)
                                .build()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



//    private List<GetNoticeListResponseDto> makeNoticeListResponse(Map<String, List<Notice>> originNoticeMap) {
//        List<GetNoticeListResponseDto> response = new ArrayList<>();
//
//        for (List<Notice> l : originNoticeMap.values()) {
//            if (!l.isEmpty()) {
//                Notice latestNotice = l.get(0);
//
//                GetNoticeListResponseDto dto = GetNoticeListResponseDto.builder()
//                        .type(latestNotice.getType())
//                        .origin(latestNotice.getOrigin())
//                        .relativeTime(ChronoUtil.timesAgo(latestNotice.getCreatedAt()))
//                        .id(latestNotice.getOriginId())
//                        .count(l.size())
//                        .build();
//
//                response.add(dto);
//            }
//        }
//
//        return response;
//    }
}
