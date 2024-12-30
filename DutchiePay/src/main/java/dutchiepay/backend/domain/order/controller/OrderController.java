package dutchiepay.backend.domain.order.controller;

import dutchiepay.backend.domain.order.dto.CancelPurchaseRequestDto;
import dutchiepay.backend.domain.order.dto.ConfirmPurchaseRequestDto;
import dutchiepay.backend.domain.order.dto.ExchangeRequestDto;
import dutchiepay.backend.domain.order.service.OrderService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "환불/교환 신청")
    @PostMapping("/exchange")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> applyExchange(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @Valid @RequestBody ExchangeRequestDto req) {
        orderService.applyExchange(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 구매 확정")
    @PatchMapping("/purchase")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> confirmPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @Valid @RequestBody ConfirmPurchaseRequestDto req) {
        orderService.confirmPurchase(userDetails.getUser(), req.getOrderId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "구매 취소")
    @PatchMapping("/exchange")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody CancelPurchaseRequestDto req) {
        orderService.cancelPurchase(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }
}
