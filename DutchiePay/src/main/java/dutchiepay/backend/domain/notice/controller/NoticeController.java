package dutchiepay.backend.domain.notice.controller;

import dutchiepay.backend.domain.notice.service.NoticeService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController("/notice")
@AllArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @PreAuthorize("isAuthenticated()")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return noticeService.subscribe(userDetails.getUser());
    }
}
