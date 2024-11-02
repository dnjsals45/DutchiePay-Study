package dutchiepay.backend.domain.profile.controller;

import dutchiepay.backend.domain.profile.dto.*;
import dutchiepay.backend.domain.profile.service.ProfileService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "프로필 API", description = "프로필 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * GET
     */
    @Operation(summary = "마이페이지 조회 (구현은 완료하였지만 주문 상태 코드 확정 X)")
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> myPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.myPage(userDetails.getUser()));
    }

    @Operation(summary = "구매 내역(배송) 조회 (구현 완료)")
    @GetMapping("/mygoods")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> myGoods(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestParam(name = "page") Long page,
                                   @RequestParam(name = "limit") Long limit) {
        return ResponseEntity.ok().body(profileService.getMyGoods(userDetails.getUser(), page, limit));
    }

    // 타입은 post / comment 중 하나
    @Operation(summary = "작성 글/댓글단 게시글 조회 (구현 완료)")
    @GetMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> myPosts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @RequestParam(name = "type") String type,
                                     @RequestParam(name = "page") Long page,
                                     @RequestParam(name = "limit") Long limit) {
        return ResponseEntity.ok().body(profileService.getMyPosts(userDetails.getUser(), type, page, limit));
    }

    @Operation(summary = "좋아요 누른 상품 조회 (구현 완료)")
    @GetMapping("/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> myLike(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.getMyLike(userDetails.getUser()));
    }

    @Operation(summary = "내가 쓴 후기 조회 (구현 완료)")
    @GetMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> myReviews(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.getMyReviews(userDetails.getUser()));
    }

    @Operation(summary = "후기 1개 조회 (구현 완료)", description = "reviewId 입력하지 않을 시 내가 쓴 후기 전체 조회")
    @GetMapping(value = "/reviews", params = "reviewId")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOneReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestParam(name = "reviewId", required = false) Long reviewId) {
        return ResponseEntity.ok().body(profileService.getOneReview(userDetails.getUser(), reviewId));
    }

    @Operation(summary = "내가 쓴 문의 내역 조회 (구현 완료)")
    @GetMapping("/asks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> myAsks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.getMyAsks(userDetails.getUser()));
    }


    /**
     * POST
     */
    @Operation(summary = "상품 후기 작성 (구현 완료)")
    @PostMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @Valid @RequestBody CreateReviewRequestDto req) {
        profileService.createReview(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 문의 작성 (구현 완료)")
    @PostMapping("/asks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createAsk(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @Valid @RequestBody CreateAskRequestDto req) {
        profileService.createAsk(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH
     */
    @Operation(summary = "닉네임 변경 (구현 완료)")
    @PatchMapping("/nickname")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody ChangeNicknameRequestDto request) {
        profileService.changeNickname(userDetails.getUser(), request.getNickname());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 이미지 변경 (구현 완료)")
    @PatchMapping("/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody ChangeProfileImgRequestDto request) {
        profileService.changeProfileImage(userDetails.getUser(), request.getProfileImg());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "지역 변경 (구현 완료)")
    @PatchMapping("/location")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeLocation(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody ChangeLocationRequestDto request) {
        profileService.changeLocation(userDetails.getUser(), request.getLocation());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전화번호 변경 (구현 완료)")
    @PatchMapping("/phone")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePhone(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @Valid @RequestBody ChangePhoneRequestDto request ) {
        profileService.changePhone(userDetails.getUser(), request.getPhone());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "후기 수정")
    @PatchMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @Valid @RequestBody UpdateReviewRequestDto request) {
        profileService.updateReview(userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE
     */
    @Operation(summary = "후기 삭제 (구현 완료)")
    @DeleteMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @RequestParam(name = "reviewId") Long reviewId) {
        profileService.deleteReview(userDetails.getUser(), reviewId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문의 삭제 (구현 완료)")
    @DeleteMapping("/asks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAsk(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @RequestParam(name = "askid") Long askId) {
        profileService.deleteAsk(userDetails.getUser(), askId);
        return ResponseEntity.ok().build();
    }
}
