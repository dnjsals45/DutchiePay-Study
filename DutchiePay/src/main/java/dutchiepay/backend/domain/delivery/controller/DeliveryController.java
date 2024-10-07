package dutchiepay.backend.domain.delivery.controller;

import dutchiepay.backend.domain.delivery.dto.ChangeDeliveryResquestDto;
import dutchiepay.backend.domain.delivery.dto.CreateDeliveryRequestDto;
import dutchiepay.backend.domain.delivery.dto.DeleteDeliveryRequestDto;
import dutchiepay.backend.domain.delivery.service.DeliveryService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "배송지 API", description = "배송지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @Operation(summary = "배송지 조회(구현 완료)")
    @GetMapping("")
    public ResponseEntity<?> getMyDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(deliveryService.getDelivery(userDetails.getUser()));
    }

    @Operation(summary = "배송지 추가(구현 완료)")
    @PostMapping("")
    public ResponseEntity<?> addDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @Valid @RequestBody CreateDeliveryRequestDto req) {
        return ResponseEntity.ok().body(deliveryService.addDelivery(userDetails.getUser(), req));
    }

    @Operation(summary = "배송지 수정(구현 완료)")
    @PatchMapping("")
    public ResponseEntity<?> updateDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody ChangeDeliveryResquestDto req) {
        deliveryService.updateDelivery(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "배송지 삭제(구현 완료)")
    @DeleteMapping("")
    public ResponseEntity<?> deleteDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody DeleteDeliveryRequestDto req) {
        deliveryService.deleteDelivery(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }
}
