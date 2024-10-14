package dutchiepay.backend.domain.commerce.controller;

import dutchiepay.backend.domain.commerce.service.CommerceService;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/commerce")
@RequiredArgsConstructor
public class CommerceController {

    private final CommerceService commerceService;

    @Operation(summary = "좋아요 기능(구현중)")
    @PatchMapping
    public ResponseEntity<Void> likes(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestBody Map<String, Long> requestBody) {
        commerceService.likes(userDetails, requestBody.get("productId"));
        return ResponseEntity.noContent().build();
    }
}
