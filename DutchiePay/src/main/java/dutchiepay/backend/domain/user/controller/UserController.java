package dutchiepay.backend.domain.user.controller;

import dutchiepay.backend.domain.user.dto.FindEmailRequestDto;
import dutchiepay.backend.domain.user.dto.FindPasswordRequestDto;
import dutchiepay.backend.domain.user.dto.NonUserChangePasswordRequestDto;
import dutchiepay.backend.domain.user.dto.PhoneAuthRequestDto;
import dutchiepay.backend.domain.user.dto.UserChangePasswordRequestDto;
import dutchiepay.backend.domain.user.dto.UserReLoginRequestDto;
import dutchiepay.backend.domain.user.dto.UserReissueRequestDto;
import dutchiepay.backend.domain.user.dto.UserSignupRequestDto;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.security.UserDetailsImpl;
import dutchiepay.backend.global.sms.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final SmsService smsService;

    @Operation(summary = "이메일 찾기", description = "휴대폰 번호를 이용한 이메일 찾기")
    @PostMapping("/email")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> findEmail(@Valid @RequestBody FindEmailRequestDto req) {
        return ResponseEntity.ok().body(userService.findEmail(req));
    }

    @Operation(summary = "닉네임 검사 중복확인", operationId = "닉네임 중복확인")
    @GetMapping(value = "", params = "nickname")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> isExistNickname(@RequestParam(required = false) String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(UserErrorCode.USER_NICKNAME_MISSING);
        }
        userService.existsNickname(nickname);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 검사 중복확인", operationId = "이메일 중복확인")
    @GetMapping(value = "", params = "email")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> isExistEmail(@RequestParam(required = false) String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(UserErrorCode.USER_EMAIL_MISSING);
        }
        userService.existsEmail(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비회원 비밀번호 찾기")
    @PostMapping("/pwd")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> findPassword(@Valid @RequestBody FindPasswordRequestDto req) {
        userService.findPassword(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비회원 비밀번호 재설정")
    @PatchMapping("/pwd-nonuser")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> changePasswordNonUser(
        @Valid @RequestBody NonUserChangePasswordRequestDto req) {
        userService.changeNonUserPassword(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 비밀번호 재설정")
    @PatchMapping("/pwd-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePasswordUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody UserChangePasswordRequestDto req) {
        userService.changeUserPassword(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> phoneAuth(@Valid @RequestBody PhoneAuthRequestDto req) {
        return ResponseEntity.ok().body(smsService.sendVerificationMessage(req.getPhone()));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        HttpServletRequest request) {

        userService.logout(userDetails.getUserId(), request);
        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           HttpServletRequest request) {
        userService.deleteUser(userDetails, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "자동로그인")
    @PostMapping("/relogin")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> reLogin(@Valid @RequestBody UserReLoginRequestDto requestDto) {
        return ResponseEntity.ok().body(userService.reLogin(requestDto.getRefresh()));
    }

    @Operation(summary = "access Token 재발급")
    @PostMapping("/reissue")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> reissue(@Valid @RequestBody UserReissueRequestDto requestDto) {
        return ResponseEntity.ok().body(userService.reissue(requestDto));
    }
}
