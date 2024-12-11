package dutchiepay.backend.domain.community.controller;

import dutchiepay.backend.domain.community.dto.CreateMartRequestDto;
import dutchiepay.backend.domain.community.dto.UpdateMartRequestDto;
import dutchiepay.backend.domain.community.service.MartService;
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

@RestController
@RequestMapping("/mart")
@RequiredArgsConstructor
public class ShareController {
    private final MartService martService;

    @Operation(summary = "마트/배달 리스트 조회")
    @GetMapping(value = "/list")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getMartList(@RequestParam String category,
                                         @RequestParam(required = false) Long cursor,
                                         @RequestParam Integer limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            user = userDetails.getUser();
        }

        return ResponseEntity.ok().body(martService.getMartList(user, category, cursor, limit));
    }

    @Operation(summary = "마트/배달 게시글 상세 조회")
    @GetMapping(value = "")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMartByShareId(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @RequestParam Long shareId) {
        return ResponseEntity.ok().body(martService.getMartByShareId(userDetails.getUser(), shareId));
    }

    @Operation(summary = "마트/배달 게시글 작성")
    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createMart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestBody @Valid CreateMartRequestDto req) {
        martService.createMart(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마트/배달 게시글 수정")
    @PatchMapping("")
    @PreAuthorize("isAuthenticated() && @shareAuthorManager.isShareAuthor(#req.shareId, authentication.getPrincipal())")
    public ResponseEntity<?> updateMart(@RequestBody UpdateMartRequestDto req) {
        martService.updateMart(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마트/배달 게시글 삭제")
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated() && @shareAuthorManager.isShareAuthor(#shareId, authentication.getPrincipal())")
    public ResponseEntity<?> deleteMart(@RequestParam Long shareId) {
        martService.deleteMart(shareId);
        return ResponseEntity.ok().build();
    }
}
