package dutchiepay.backend.domain.notice.controller;

import dutchiepay.backend.domain.notice.dto.NoticeIdDto;
import dutchiepay.backend.domain.notice.service.NoticeService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notice")
@AllArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @Operation(summary = "알림 리스트 조회", description = "최근 7일간 발생한 알림 리스트를 조회합니다.")
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getNotices(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(noticeService.getNotices(userDetails.getUser()));
    }

    @Operation(summary = "알림 구독", description = "응답으로 읽지않은 알림이 있는지 없는지 여부 보내줌")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(noticeService.subscribe(userDetails.getUser()));
    }

    @Operation(summary = "알림 전체 삭제", description = "알림을 읽음처리 합니다.")
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAllNotices(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        noticeService.readAllNotices(userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 추가 목록")
    @GetMapping(value = "", params = "noticeId")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMoreNotices(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestParam(required = false) Long noticeId) {
        return ResponseEntity.ok(noticeService.getMoreNotices(userDetails.getUser(), noticeId));
    }

    @Operation(summary = "알림 읽음 개별 처리")
    @PatchMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> readNotice(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestBody NoticeIdDto noticeIdDto) {
        noticeService.readSingleNotice(userDetails.getUser(), noticeIdDto.getNoticeId());
        return ResponseEntity.ok().build();
    }
}
