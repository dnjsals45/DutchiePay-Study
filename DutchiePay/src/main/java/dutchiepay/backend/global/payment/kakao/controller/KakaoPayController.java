package dutchiepay.backend.global.payment.kakao.controller;

import dutchiepay.backend.global.payment.kakao.dto.ApproveResponseDto;
import dutchiepay.backend.global.payment.kakao.dto.ReadyRequestDto;
import dutchiepay.backend.global.payment.kakao.service.KakaoPayService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/pay/kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoPayController {
    private final String PAYMENT_APPROVE_STATUS = "PAYMENT_APPROVED";
    private final String PAYMENT_CANCEL_STATUS = "PAYMENT_CANCELED";
    private final String PAYMENT_FAIL_STATUS = "PAYMENT_FAILED";
    private final KakaoPayService kakaoPayService;

    @PostMapping("/ready")
    public ResponseEntity<?> ready(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestBody ReadyRequestDto req) {
        return ResponseEntity.ok().body(kakaoPayService.kakaoPayReady(userDetails.getUser(), req));
    }

    @GetMapping("/approve")
    public void approve(HttpServletResponse response,
                        @RequestParam("pg_token") String pgToken,
                        @RequestParam("orderNum") String orderNum) throws IOException {
        ApproveResponseDto result = kakaoPayService.kakaoPayApprove(pgToken, orderNum);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(kakaoPayService.makeApproveHtml(orderNum, result.getAmount().getTotal(), PAYMENT_APPROVE_STATUS));
        response.getWriter().flush();
    }

    @GetMapping("/cancel")
    public String cancel() {
        // 주문건이 진짜 취소되었는지 확인 후 취소 처리
        // 결제내역조회(/v1/payment/status) api에서 status를 확인한다.
        return "";
    }

    @GetMapping("/fail")
    public String fail() {
        // 주문건이 진짜 실패되었는지 확인 후 실패 처리
        // 결제내역조회(/v1/payment/status) api에서 status를 확인한다.
        return "";
    }
}
