package dutchiepay.backend.domain.profile.controller;

import dutchiepay.backend.domain.profile.dto.ChangeAddressRequestDto;
import dutchiepay.backend.domain.profile.dto.CreateAskRequestDto;
import dutchiepay.backend.domain.profile.dto.CreateReviewRequestDto;
import dutchiepay.backend.domain.profile.service.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
// TODO 아직 UserDetailsService 구현 전이라 임시로 Long userId로 사용. 구현 완료 시 UserDetails 사용
public class ProfileController {

    private final ProfileService profileService;

    /**
     * GET
     */
    @GetMapping("")
    public ResponseEntity<?> myPage(Long userId) {
        return ResponseEntity.ok().body(profileService.myPage(userId));
    }

    @GetMapping("/mygoods")
    public ResponseEntity<?> myGoods(Long userId,
                                   @RequestParam(name = "page") Long page,
                                   @RequestParam(name = "limit") Long limit) {
        return ResponseEntity.ok().body(profileService.getMyGoods(userId, page, limit));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> myPosts(Long userId,
                                     @RequestParam(name = "page") Long page,
                                     @RequestParam(name = "limit") Long limit) {
        return ResponseEntity.ok().body(profileService.getMyPosts(userId, page, limit));
    }

    @GetMapping("/like")
    public ResponseEntity<?> myLike(Long userId,
                                    @RequestParam(name = "category") String category) {
        return ResponseEntity.ok().body(profileService.getMyLike(userId, category));
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> myReviews(Long userId) {
        return ResponseEntity.ok().body(profileService.getMyReviews(userId));
    }

    @GetMapping("/asks")
    public ResponseEntity<?> myAsks(Long userId) {
        return ResponseEntity.ok().body(profileService.getMyAsks(userId));
    }


    /**
     * POST
     */
    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(Long userId,
                                          @Valid @RequestBody CreateReviewRequestDto req) {
        return ResponseEntity.ok().body(profileService.createReview(userId, req));
    }

    @PostMapping("/asks")
    public ResponseEntity<?> createAsk(Long userId,
                                       @Valid @RequestBody CreateAskRequestDto req) {
        return ResponseEntity.ok().body(profileService.createAsk(userId, req));
    }

    /**
     * PATCH
     */
    @PatchMapping("/nickname")
    public ResponseEntity<?> changeNickname(Long userId, @RequestBody String nickname) {
        profileService.changeNickname(userId, nickname);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/image")
    public ResponseEntity<?> changeProfileImage(Long userId, @RequestBody String profileImg) {
        profileService.changeProfileImage(userId, profileImg);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/location")
    public ResponseEntity<?> changeLocation(Long userId, @RequestBody String location) {
        profileService.changeLocation(userId, location);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/address")
    public ResponseEntity<?> changeAddress(Long userId, @Valid @RequestBody ChangeAddressRequestDto req) {
        profileService.changeAddress(userId, req);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/phone")
    public ResponseEntity<?> changePhone(Long userId,
                                         @NotBlank
                                         @Pattern(regexp = "^\\d{11}$", message = "전화번호는 11자리 숫자여야 합니다.")
                                         @RequestBody String phone) {
        profileService.changePhone(userId, phone);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE
     */
    @DeleteMapping("/asks")
    public ResponseEntity<?> deleteAsk(Long userId, @RequestBody Long reviewId) {
        return ResponseEntity.ok().build();
    }
}
