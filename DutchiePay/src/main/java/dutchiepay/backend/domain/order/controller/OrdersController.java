package dutchiepay.backend.domain.order.controller;

import dutchiepay.backend.domain.order.dto.CancelPurchaseRequestDto;
import dutchiepay.backend.domain.order.dto.ConfirmPurchaseRequestDto;
import dutchiepay.backend.domain.order.dto.ExchangeRequestDto;
import dutchiepay.backend.domain.order.service.OrdersService;
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
public class OrdersController {
    private final OrdersService ordersService;

    @Operation(summary = "환불/교환 신청 (구현 완료)")
    @PostMapping("/exchange")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> applyExchange(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @Valid @RequestBody ExchangeRequestDto req) {
        ordersService.applyExchange(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 구매 확정 (구현 완료)")
    @PatchMapping("/purchase")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> confirmPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @Valid @RequestBody ConfirmPurchaseRequestDto req) {
        ordersService.confirmPurchase(userDetails.getUser(), req.getOrderId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "구매 취소 (구현 완료)")
    @PatchMapping("/exchange")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody CancelPurchaseRequestDto req) {
        ordersService.cancelPurchase(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }
}
