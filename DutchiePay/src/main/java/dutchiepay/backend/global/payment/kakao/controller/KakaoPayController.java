package dutchiepay.backend.global.payment.kakao.controller;

import dutchiepay.backend.global.payment.kakao.dto.ApproveRequestDto;
import dutchiepay.backend.global.payment.kakao.dto.ReadyRequestDto;
import dutchiepay.backend.global.payment.kakao.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay/kakao")
@RequiredArgsConstructor
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    @GetMapping("/ready")
    public ResponseEntity<?> ready(@RequestBody ReadyRequestDto dto) {
        return ResponseEntity.ok().body(kakaoPayService.kakaoPayReady(dto));
    }

    @GetMapping("/approve")
    public ResponseEntity<?> approve(@RequestParam("pg_token") String pgToken, @RequestBody ApproveRequestDto req) {
        return ResponseEntity.ok().body(kakaoPayService.kakaoPayApprove(pgToken, req));
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
