package dutchiepay.backend.domain.user.controller;

import dutchiepay.backend.domain.user.dto.UserSignupRequestDto;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import dutchiepay.backend.domain.user.dto.*;
import dutchiepay.backend.global.sms.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final SmsService smsService;

    @Operation(summary = "이메일 찾기(구현 완료)", description = "휴대폰 번호를 이용한 이메일 찾기")
    @PostMapping("/email")
    public ResponseEntity<?> findEmail(@Valid @RequestBody FindEmailRequestDto req) {
        return ResponseEntity.ok().body(userService.findEmail(req));
    }

    @Operation(summary = "비회원 비밀번호 찾기(구현 완료)")
    @PostMapping("/pwd")
    public ResponseEntity<?> findPassword(@Valid @RequestBody FindPasswordRequestDto req) {
        userService.findPassword(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비회원 비밀번호 재설정(구현 완료)")
    @PatchMapping("/pwd-nonuser")
    public ResponseEntity<?> changePasswordNonUser(@Valid @RequestBody NonUserChangePasswordRequestDto req) {
        userService.changeNonUserPassword(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 비밀번호 재설정(구현 완료)")
    @PatchMapping("/pwd-user")
    public ResponseEntity<?> changePasswordUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @Valid @RequestBody UserChangePasswordRequestDto req) {
        userService.changeUserPassword(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
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
