package dutchiepay.backend.global.payment.kakao.controller;

import dutchiepay.backend.global.payment.kakao.dto.ApproveResponseDto;
import dutchiepay.backend.global.payment.kakao.dto.ReadyRequestDto;
import dutchiepay.backend.global.payment.kakao.dto.ReadyResponseDto;
import dutchiepay.backend.global.payment.kakao.service.KakaoPayService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/pay/kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoPayController {
    private final String PAYMENT_APPROVE_STATUS = "PAYMENT_APPROVED";
    private final String PAYMENT_CANCEL_STATUS = "PAYMENT_CANCEL";
    private final String PAYMENT_FAIL_STATUS = "PAYMENT_FAIL";
    private final String POST_MESSAGE_CONTENT_TYPE = "text/html;charset=UTF-8";
    private final KakaoPayService kakaoPayService;

    @PostMapping("/ready")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> ready(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestBody ReadyRequestDto req) {
        return ResponseEntity.ok().body(kakaoPayService.kakaoPayReady(userDetails.getUser(), req));
    }

    @GetMapping("/approve")
    @PreAuthorize("permitAll()")
    public void approve(HttpServletResponse response,
                        @RequestParam("pg_token") String pgToken,
                        @RequestParam("orderNum") String orderNum) throws IOException {
        ApproveResponseDto result = kakaoPayService.kakaoPayApprove(pgToken, orderNum);

        response.setContentType(POST_MESSAGE_CONTENT_TYPE);
        response.getWriter().write(kakaoPayService.makeApproveHtml(orderNum, result.getAmount().getTotal(), PAYMENT_APPROVE_STATUS));
        response.getWriter().flush();
    }

    @GetMapping("/cancel")
    @PreAuthorize("permitAll()")
    public String cancel(HttpServletResponse response,
                         @RequestParam("orderNum") String orderNum) throws IOException {
        // 결제내역조회(/v1/payment/status) api에서 status를 확인한다.
        String status = kakaoPayService.kakaPayCheckStatus(orderNum);

        // 승인되지 않은 경우(결제 x인 경우) -> 취소 불가능 (상태코드 : READY)
        // 승인 된 경우 -> 취소 가능 (상태코드 : SUCCESS_PAYMENT)
        // 이미 취소 된 경우 -> 취소 불가능 (상태코드 : CANCEL_PAYMENT)
        if (status.equals("SUCCESS_PAYMENT")) {
            kakaoPayService.kakaoPayCancel(orderNum);

            response.setContentType(POST_MESSAGE_CONTENT_TYPE);
            response.getWriter().write(kakaoPayService.makeCancelHtml(orderNum, PAYMENT_CANCEL_STATUS));
            response.getWriter().flush();
        } else if (status.equals("CANCEL_PAYMENT")) {
            return "";
        }

        return "";
    }

    @GetMapping("/fail")
    @PreAuthorize("permitAll()")
    public String fail(HttpServletResponse response,
                       @RequestParam("orderNum") String orderNum) throws IOException {
        String status = kakaoPayService.kakaPayCheckStatus(orderNum);

        if (status.equals("FAIL_PAYMENT")) {
            kakaoPayService.kakaoPayFail(orderNum);
            response.setContentType(POST_MESSAGE_CONTENT_TYPE);
            response.getWriter().write(kakaoPayService.makeFailHtml(orderNum, PAYMENT_FAIL_STATUS));
            response.getWriter().flush();
        }
        return "";
    }
}
