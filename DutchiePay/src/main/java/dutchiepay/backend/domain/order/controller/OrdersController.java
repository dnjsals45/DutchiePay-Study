package dutchiepay.backend.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    @Operation(summary = "환불/교환 신청 (구현 중)")
    @PostMapping("/exchange")
    public ResponseEntity<?> applyExchange() {
        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "상품 구매 확정 (구현 중)")
    @PatchMapping("/purchase")
    public ResponseEntity<?> confirmPurchase() {
        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "구매 취소 (구현 중)")
    @PatchMapping("/exchange")
    public ResponseEntity<?> cancelExchange() {
        return ResponseEntity.ok().body(null);
    }
}
