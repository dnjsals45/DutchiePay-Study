package dutchiepay.backend.domain.community.controller;

import dutchiepay.backend.domain.community.dto.*;
import dutchiepay.backend.domain.community.service.PurchaseService;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trading")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "나눔/거래 리스트 조회")
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PurchaseListResponseDto> getPurchaseList(@RequestParam(value = "category", required = false) String category,
                                                                   @RequestParam(value = "word", required = false) String keyword,
                                                                   @RequestParam("limit") int limit,
                                                                   @RequestParam(value = "cursor", required = false) Long cursor) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            user = userDetails.getUser();
        }
        return ResponseEntity.ok(purchaseService.getPurchaseList(user, category, keyword, limit, cursor));
    }

    @Operation(summary = "나눔/거래 상세 조회")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PurchaseResponseDto> getPurchase(@RequestParam("purchaseId") Long purchaseId) {
        return ResponseEntity.ok(purchaseService.getPurchase(purchaseId));
    }

    @Operation(summary = "나눔/거래 게시글 작성")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> createPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @Valid @RequestBody CreatePurchaseRequestDto requestDto) {
        return ResponseEntity.ok(purchaseService.createPurchase(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "나눔/거래 게시글 상세 조회(수정용)")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PurchaseForUpdateDto> getPurchaseForUpdate(@PathVariable("id") Long purchaseId) {
        return ResponseEntity.ok(purchaseService.getPurchaseForUpdate(purchaseId));
    }

    @Operation(summary = "나눔/거래 게시글 수정")
    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @Valid @RequestBody UpdatePurchaseRequestDto updateDto) {
        purchaseService.updatePurchase(userDetails.getUser(), updateDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "나눔/거래 게시글 삭제")
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestParam("purchaseId") Long purchaseId) {
        purchaseService.deletePurchase(userDetails.getUser(), purchaseId);
        return ResponseEntity.noContent().build();
    }
}
