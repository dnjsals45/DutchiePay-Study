package dutchiepay.backend.domain.user.controller;

import dutchiepay.backend.domain.user.dto.UserSignupRequestDto;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import dutchiepay.backend.domain.user.dto.*;
import dutchiepay.backend.global.sms.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final SmsService smsService;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        User testUser = User.builder()
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
    public ResponseEntity<?> findEmail(@Valid @RequestBody FindEmailRequestDto req) {
        return ResponseEntity.ok().body(userService.findEmail(req));
    }

    @PostMapping("/pwd")
    public ResponseEntity<?> findPassword(@Valid @RequestBody FindPasswordRequestDto req) {
        userService.findPassword(req);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/pwd-nonuser")
    public ResponseEntity<?> changePasswordNonUser(@Valid @RequestBody NonUserChangePasswordRequestDto req) {
        userService.changeNonUserPassword(req);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/pwd-user")
    public ResponseEntity<?> changePasswordUser(@Valid @RequestBody UserChangePasswordRequestDto req) {
        return ResponseEntity.ok().body(userService.changeUserPassword(req));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> phoneAuth(@Valid @RequestBody PhoneAuthRequestDto req) {
        return ResponseEntity.ok().body(smsService.sendVerificationMessage(req.getPhone()));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        try {
            userService.signup(requestDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(null);
    }


}
