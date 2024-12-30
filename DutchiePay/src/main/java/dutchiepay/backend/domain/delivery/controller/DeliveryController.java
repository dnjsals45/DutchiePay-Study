package dutchiepay.backend.domain.delivery.controller;

import dutchiepay.backend.domain.delivery.dto.ChangeDeliveryRequestDto;
import dutchiepay.backend.domain.delivery.dto.CreateDeliveryRequestDto;
import dutchiepay.backend.domain.delivery.service.DeliveryService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "배송지 API", description = "배송지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @Operation(summary = "배송지 조회")
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(deliveryService.getDelivery(userDetails.getUser()));
    }

    @Operation(summary = "배송지 추가")
    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @Valid @RequestBody CreateDeliveryRequestDto req) {
        return ResponseEntity.ok().body(deliveryService.addDelivery(userDetails.getUser(), req));
    }

    @Operation(summary = "배송지 수정")
    @PatchMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody ChangeDeliveryRequestDto req) {
        deliveryService.updateDelivery(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "배송지 삭제")
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestParam(name = "addressid") Long addressId) {
        deliveryService.deleteDelivery(userDetails.getUser(), addressId);
        return ResponseEntity.ok().build();
    }
}
