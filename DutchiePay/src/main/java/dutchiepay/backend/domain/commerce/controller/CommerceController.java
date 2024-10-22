package dutchiepay.backend.domain.commerce.controller;

import dutchiepay.backend.domain.commerce.dto.AddEntityDto;
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

    @Operation(summary = "공동구매 리스트 조회(구현중)")
    @GetMapping(value = "/list", params = {"filter", "category", "end", "cursor", "limit"})
    public ResponseEntity<?> getBuyList(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestParam("filter") String filter,
                                        @RequestParam("category") String category,
                                        @RequestParam("end") int end,
                                        @RequestParam("cursor") Long cursor,
                                        @RequestParam("limit") int limit) {
        return ResponseEntity.ok().body(commerceService.getBuyList(userDetails.getUser(), filter, category, end, cursor, limit));
    }

    @Operation(summary = "공동구매 상품 상세 페이지(구현중)")
    @GetMapping(value = "", params = "buyId")
    public ResponseEntity<?> getBuyPage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestParam("buyId") Long buyId) {
        return ResponseEntity.ok().body(commerceService.getBuyPage(userDetails.getUser(), buyId));
    }

    @Operation(summary = "상품 후기 조회(구현중)")
    @GetMapping("/review")
    public ResponseEntity<?> getProductReview(@RequestParam("productId") Long productId,
                                              @RequestParam("photo") Long photo,
                                              @RequestParam("page") Long page,
                                              @RequestParam("limit") Long limit) {
        return ResponseEntity.ok().body(commerceService.getProductReview(productId, photo, page, limit));
    }

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

    @PostMapping("/addition")
    public ResponseEntity<Void> addEntity(@RequestBody AddEntityDto addEntityDto) {
        commerceService.addEntity(addEntityDto);
        return ResponseEntity.ok().build();
    }
}
