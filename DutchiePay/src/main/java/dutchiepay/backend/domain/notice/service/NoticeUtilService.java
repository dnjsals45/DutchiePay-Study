package dutchiepay.backend.domain.notice.service;

import dutchiepay.backend.domain.community.service.CommunityUtilService;
import dutchiepay.backend.domain.notice.repository.NoticeRepository;
import dutchiepay.backend.entity.Comment;
import dutchiepay.backend.entity.Notice;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeUtilService {
    private final NoticeRepository noticeRepository;
    private final CommunityUtilService communityUtilService;

    public List<Notice> findRecentNotices(User user, LocalDateTime time) {
        return noticeRepository.findRecentNotices(user, time);
    }

    public List<Notice> findByUserAndIsReadFalseAndCreatedAtAfter(User user, LocalDateTime minus) {
        return noticeRepository.findByUserAndIsReadFalseAndCreatedAtAfter(user, minus);
    }

    public Map<String, List<Notice>> makeNoticeMapByOrigin(List<Notice> notices) {
        Map<String, List<Notice>> noticeMap = new HashMap<>();

        for (Notice n : notices) {
            if (!noticeMap.containsKey(n.getOrigin())) {
                noticeMap.put(n.getOrigin(), new ArrayList<>());
            }

            noticeMap.get(n.getOrigin()).add(n);
        }

        return noticeMap;
    }

    public Notice createCommentNotice(String writer, Comment comment) {
        if (comment.getParentId() == null) {
            return createPostCommentNotice(writer, comment);
        } else {
            return createReplyCommentNotice(writer, comment);
        }
    }

    private Notice createPostCommentNotice(String writer, Comment comment) {
        return noticeRepository.save(Notice.builder()
                .user(comment.getFree().getUser())
                .type("comment")
                .origin(comment.getFree().getTitle())
                .originId(comment.getFree().getFreeId())
                .writer(writer)
                .isRead(false)
                .build());
    }

    private Notice createReplyCommentNotice(String writer, Comment comment) {
        Comment c = communityUtilService.findCommentById(comment.getParentId());
        return noticeRepository.save(Notice.builder()
                .user(c.getUser())
                .type("reply")
                .origin(c.getContents())
                .originId(c.getCommentId())
                .writer(writer)
                .isRead(false)
                .build());
    }
}
