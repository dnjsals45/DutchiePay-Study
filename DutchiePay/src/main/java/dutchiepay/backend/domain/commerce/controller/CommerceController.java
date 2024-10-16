package dutchiepay.backend.domain.commerce.controller;

import dutchiepay.backend.domain.commerce.dto.PaymentInfoResponseDto;
import dutchiepay.backend.domain.commerce.service.CommerceService;
import dutchiepay.backend.domain.commerce.dto.BuyAskResponseDto;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/commerce")
@RequiredArgsConstructor
public class CommerceController {

    private final CommerceService commerceService;

    @Operation(summary = "상품 좋아요 기능(구현중)")
    @PatchMapping
    public ResponseEntity<Void> likes(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestBody Map<String, Long> requestBody) {
        commerceService.likes(userDetails, requestBody.get("productId"));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품 문의내역 조회(구현중)")
    @GetMapping("/asks")
    public ResponseEntity<List<BuyAskResponseDto>> getBuyAsks(@RequestParam("productId") Long buyId,
                                                              Pageable pageable) {
        return ResponseEntity.ok(commerceService.getBuyAsks(buyId, pageable).getContent()
                .stream().map(BuyAskResponseDto::toDto)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "결제 정보 불러오기(구현중)")
    @GetMapping("/delivery")
    public ResponseEntity<PaymentInfoResponseDto> getPaymentInfo(@RequestParam("buypostid") Long buyId) {
        return ResponseEntity.ok(commerceService.getPaymentInfo(buyId));
    }
}
