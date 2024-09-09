package dutchiepay.backend.domain.profile.controller;

import dutchiepay.backend.domain.profile.dto.ChangeAddressRequestDto;
import dutchiepay.backend.domain.profile.dto.CreateAskRequestDto;
import dutchiepay.backend.domain.profile.dto.CreateReviewRequestDto;
import dutchiepay.backend.domain.profile.service.ProfileService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * GET
     */
    @GetMapping("")
    public ResponseEntity<?> myPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.myPage(userDetails.getUser()));
    }

    @GetMapping("/mygoods")
    public ResponseEntity<?> myGoods(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestParam(name = "page") Long page,
                                   @RequestParam(name = "limit") Long limit) {
        return ResponseEntity.ok().body(profileService.getMyGoods(userDetails.getUser(), page, limit));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> myPosts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @RequestParam(name = "page") Long page,
                                     @RequestParam(name = "limit") Long limit) {
        return ResponseEntity.ok().body(profileService.getMyPosts(userDetails.getUser(), page, limit));
    }

    @GetMapping("/like")
    public ResponseEntity<?> myLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @RequestParam(name = "category") String category) {
        return ResponseEntity.ok().body(profileService.getMyLike(userDetails.getUser(), category));
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> myReviews(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.getMyReviews(userDetails.getUser()));
    }

    @GetMapping("/asks")
    public ResponseEntity<?> myAsks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(profileService.getMyAsks(userDetails.getUser()));
    }


    /**
     * POST
     */
    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @Valid @RequestBody CreateReviewRequestDto req) {
        profileService.createReview(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/asks")
    public ResponseEntity<?> createAsk(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @Valid @RequestBody CreateAskRequestDto req) {
        profileService.createAsk(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH
     */
    @PatchMapping("/nickname")
    public ResponseEntity<?> changeNickname(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String nickname) {
        profileService.changeNickname(userDetails.getUser(), nickname);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/image")
    public ResponseEntity<?> changeProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String profileImg) {
        profileService.changeProfileImage(userDetails.getUser(), profileImg);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/location")
    public ResponseEntity<?> changeLocation(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String location) {
        profileService.changeLocation(userDetails.getUser(), location);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/address")
    public ResponseEntity<?> changeAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody ChangeAddressRequestDto req) {
        profileService.changeAddress(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

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
    @DeleteMapping("/asks")
    public ResponseEntity<?> deleteAsk(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Long askId) {
        profileService.deleteAsk(userDetails.getUser(), askId);
        return ResponseEntity.ok().build();
    }
}
