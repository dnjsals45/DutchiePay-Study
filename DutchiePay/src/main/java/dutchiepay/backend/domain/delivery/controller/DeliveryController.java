package dutchiepay.backend.domain.delivery.controller;

import dutchiepay.backend.domain.delivery.service.DeliveryService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "배송지 API", description = "프로필 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @Operation(summary = "배송지 조회")
    @GetMapping("")
    public ResponseEntity<?> getMyDelivery(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(deliveryService.getDelivery(userDetails.getUser()));
    }

}
