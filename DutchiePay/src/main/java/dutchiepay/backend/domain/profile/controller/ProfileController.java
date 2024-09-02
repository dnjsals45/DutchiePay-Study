package dutchiepay.backend.domain.profile.controller;

import dutchiepay.backend.domain.profile.dto.CreateReviewRequestDto;
import dutchiepay.backend.domain.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
// TODO 아직 UserDetailsService 구현 전이라 임시로 Long userId로 사용
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
    public ResponseEntity<?> createAsk() {
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH
     */
    @PatchMapping("/nickname")
    public ResponseEntity<?> changeNickname() {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/image")
    public ResponseEntity<?> changeProfileImage() {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/location")
    public ResponseEntity<?> changeLocation() {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/address")
    public ResponseEntity<?> changeAddress() {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/phone")
    public ResponseEntity<?> changePhone() {
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE
     */
    @DeleteMapping("/asks")
    public ResponseEntity<?> deleteAsk() {
        return ResponseEntity.ok().build();
    }
}
