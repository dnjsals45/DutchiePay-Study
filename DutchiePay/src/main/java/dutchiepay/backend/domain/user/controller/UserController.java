package dutchiepay.backend.domain.user.controller;

import dutchiepay.backend.domain.user.dto.FindEmailReq;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Users testUser = Users.builder()
                .email("test@example.com")
                .username("테스트")
                .phone("01012345678")
                .nickname("테스트 유저")
                .location("서울시 마포구 신수동")
                .state(0)
                .build();

        return ResponseEntity.ok().body(testUser);
    }


    @PostMapping("/email")
    public ResponseEntity<?> findEmail(@Valid @RequestBody FindEmailReq req) {
        return ResponseEntity.ok().body(userService.findEmail(req));
    }

    @PostMapping("/pwd")
    public ResponseEntity<?> findPassword() {
        return null;
    }

    @PatchMapping("/pwd-nonuser")
    public ResponseEntity<?> changePasswordNonUser() {
        return null;
    }

    @PatchMapping("/pwd-user")
    public ResponseEntity<?> changePasswordUser() {
        return null;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> phoneAuth() {
        return null;
    }
}
