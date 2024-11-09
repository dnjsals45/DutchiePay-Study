package dutchiepay.backend.global.payment.controller;

import dutchiepay.backend.global.payment.dto.kakao.ApproveResponseDto;
import dutchiepay.backend.global.payment.dto.kakao.ReadyRequestDto;
import dutchiepay.backend.global.payment.dto.portone.TossPaymentsSuccessResponseDto;
import dutchiepay.backend.global.payment.dto.portone.TossPaymentsValidateRequestDto;
import dutchiepay.backend.global.payment.service.KakaoPayService;
import dutchiepay.backend.global.payment.service.TossPaymentsService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final String PAYMENT_APPROVE_STATUS = "PAYMENT_APPROVED";
    private final String PAYMENT_CANCEL_STATUS = "PAYMENT_CANCEL";
    private final String PAYMENT_FAIL_STATUS = "PAYMENT_FAIL";
    private final String POST_MESSAGE_CONTENT_TYPE = "text/html;charset=UTF-8";
    private final KakaoPayService kakaoPayService;
    private final TossPaymentsService tossService;

    @PostMapping("/ready")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> ready(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestBody ReadyRequestDto req,
                                   @RequestParam String type) {
        if (type.equals("kakao")) {
            return ResponseEntity.ok().body(kakaoPayService.kakaoPayReady(userDetails.getUser(), req));
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/kakao/approve")
    @PreAuthorize("permitAll()")
    public void approve(HttpServletResponse response,
                        @RequestParam("pg_token") String pgToken,
                        @RequestParam("orderNum") String orderNum) throws IOException {
        ApproveResponseDto result = kakaoPayService.kakaoPayApprove(pgToken, orderNum);

        response.setContentType(POST_MESSAGE_CONTENT_TYPE);
        response.getWriter().write(kakaoPayService.makePostMessage(orderNum, PAYMENT_APPROVE_STATUS));
        response.getWriter().flush();
    }

    @GetMapping("/kakao/cancel")
    @PreAuthorize("permitAll()")
    public void cancel(HttpServletResponse response,
                         @RequestParam("orderNum") String orderNum) throws IOException {
        // 결제내역조회(/v1/payment/status) api에서 status를 확인한다.
        String status = kakaoPayService.kakaPayCheckStatus(orderNum);

        // 승인되지 않은 경우(결제 x인 경우) -> 취소 불가능 (상태코드 : READY)
        // 승인 된 경우 -> 취소 가능 (상태코드 : SUCCESS_PAYMENT)
        // 이미 취소 된 경우 -> 취소 불가능 (상태코드 : CANCEL_PAYMENT)
        if (status.equals("SUCCESS_PAYMENT")) {
            kakaoPayService.kakaoPayCancel(orderNum);

            response.setContentType(POST_MESSAGE_CONTENT_TYPE);
            response.getWriter().write(kakaoPayService.makePostMessage(orderNum, PAYMENT_CANCEL_STATUS));
            response.getWriter().flush();
        } else if (status.equals("CANCEL_PAYMENT")) {
        }
    }

    @GetMapping("/kakao/fail")
    @PreAuthorize("permitAll()")
    public void fail(HttpServletResponse response,
                       @RequestParam("orderNum") String orderNum) throws IOException {
        String status = kakaoPayService.kakaPayCheckStatus(orderNum);

        if (status.equals("FAIL_PAYMENT")) {
            kakaoPayService.kakaoPayFail(orderNum);
            response.setContentType(POST_MESSAGE_CONTENT_TYPE);
            response.getWriter().write(kakaoPayService.makePostMessage(orderNum, PAYMENT_FAIL_STATUS));
            response.getWriter().flush();
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TossPaymentsSuccessResponseDto> validateTossResult(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                             @RequestParam("type") String type,
                                                                             @RequestBody TossPaymentsValidateRequestDto validateRequestDto) {
        return new ResponseEntity<>(tossService.validateResult(userDetails.getUser(), validateRequestDto),
                HttpStatusCode.valueOf(200));
    }


}
