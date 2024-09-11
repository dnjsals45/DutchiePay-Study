package dutchiepay.backend.domain.profile.controller;

import dutchiepay.backend.domain.profile.dto.ChangeAddressRequestDto;
import dutchiepay.backend.domain.profile.dto.CreateAskRequestDto;
import dutchiepay.backend.domain.profile.dto.CreateReviewRequestDto;
import dutchiepay.backend.domain.profile.service.ProfileService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> myPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.myPage(userDetails.getUser()));
    }

    @Operation(summary = "구매 내역(배송) 조회 (구현 완료)")
    @GetMapping("/mygoods")
    public ResponseEntity<?> myGoods(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestParam(name = "page") Long page,
                                   @RequestParam(name = "limit") Long limit) {
        return ResponseEntity.ok().body(profileService.getMyGoods(userDetails.getUser(), page, limit));
    }

    // 타입은 post / comment 중 하나
    @Operation(summary = "작성 글/댓글단 게시글 조회 (구현 완료)")
    @GetMapping("/posts")
    public ResponseEntity<?> myPosts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @RequestParam(name = "type") String type,
                                     @RequestParam(name = "page") Long page,
                                     @RequestParam(name = "limit") Long limit) {
        return ResponseEntity.ok().body(profileService.getMyPosts(userDetails.getUser(), type, page, limit));
    }

    @Operation(summary = "좋아요 누른 상품 조회 (구현 완료)")
    @GetMapping("/like")
    public ResponseEntity<?> myLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @RequestParam(name = "category") String category) {
        return ResponseEntity.ok().body(profileService.getMyLike(userDetails.getUser(), category));
    }

    @Operation(summary = "내가 쓴 후기 조회 (구현 완료)")
    @GetMapping("/reviews")
    public ResponseEntity<?> myReviews(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.getMyReviews(userDetails.getUser()));
    }

    @Operation(summary = "후기 1개 조회 (구현 완료)")
    @GetMapping(value = "/reviews", params = "reviewId")
    public ResponseEntity<?> getOneReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestParam(name = "reviewId") Long reviewId) {
        return ResponseEntity.ok().body(profileService.getOneReview(userDetails.getUser(), reviewId));
    }

    @Operation(summary = "내가 쓴 문의 내역 조회 (구현 완료)")
    @GetMapping("/asks")
    public ResponseEntity<?> myAsks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.getMyAsks(userDetails.getUser()));
    }


    /**
     * POST
     */
    @Operation(summary = "상품 후기 작성 (구현 완료)")
    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @Valid @RequestBody CreateReviewRequestDto req) {
        profileService.createReview(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 문의 작성 (구현 완료)")
    @PostMapping("/asks")
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
    public ResponseEntity<?> changeNickname(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String nickname) {
        profileService.changeNickname(userDetails.getUser(), nickname);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 이미지 변경 (구현 완료)")
    @PatchMapping("/image")
    public ResponseEntity<?> changeProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String profileImg) {
        profileService.changeProfileImage(userDetails.getUser(), profileImg);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "지역 변경 (구현 완료)")
    @PatchMapping("/location")
    public ResponseEntity<?> changeLocation(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String location) {
        profileService.changeLocation(userDetails.getUser(), location);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "배송지 정보 변경 (구현 완료)")
    @PatchMapping("/address")
    public ResponseEntity<?> changeAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody ChangeAddressRequestDto req) {
        profileService.changeAddress(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전화번호 변경 (구현 완료)")
    @PatchMapping("/phone")
    public ResponseEntity<?> changePhone(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @NotBlank
                                         @Pattern(regexp = "^\\d{11}$", message = "전화번호는 11자리 숫자여야 합니다.")
                                         @RequestBody String phone) {
        profileService.changePhone(userDetails.getUser(), phone);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE
     */
    @Operation(summary = "후기 삭제 (구현 완료)")
    @DeleteMapping("/asks")
    public ResponseEntity<?> deleteAsk(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Long askId) {
        profileService.deleteAsk(userDetails.getUser(), askId);
        return ResponseEntity.ok().build();
    }
}
