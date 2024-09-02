package dutchiepay.backend.domain.order.controller;

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
    @PostMapping("/exchange")
    public ResponseEntity<?> applyExchange() {
        return ResponseEntity.ok().body(null);
    }

    @PatchMapping("/purchase")
    public ResponseEntity<?> confirmPurchase() {
        return ResponseEntity.ok().body(null);
    }

    @PatchMapping("/exchange")
    public ResponseEntity<?> cancelExchange() {
        return ResponseEntity.ok().body(null);
    }
}
