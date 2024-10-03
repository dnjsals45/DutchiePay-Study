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

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final SmsService smsService;

    @Operation(summary = "이메일 찾기(구현 완료)", description = "휴대폰 번호를 이용한 이메일 찾기")
    @PostMapping("/email")
    public ResponseEntity<?> findEmail(@Valid @RequestBody FindEmailRequestDto req) {
        return ResponseEntity.ok().body(userService.findEmail(req));
    }

    @Operation(summary = "닉네임 검사 중복확인(구현 완료)")
    @GetMapping
    public ResponseEntity<?> isExistNickname(@RequestParam(required = false) String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(UserErrorCode.USER_NICKNAME_MISSING);
        }
        try {
            userService.existsNickname(nickname);
        } catch (UserErrorException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "이메일 검사 중복확인(구현 완료)", description = "소셜 가입 이메일 제외, 이메일 회원가입만 중복 체크")
    @GetMapping
    public ResponseEntity<?> isExistEmail(@RequestParam(required = false) String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(UserErrorCode.USER_EMAIL_MISSING);
        }
        try {
            userService.existsEmail(email);
        } catch (UserErrorException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "비회원 비밀번호 찾기(구현 완료)")
    @PostMapping("/pwd")
    public ResponseEntity<?> findPassword(@Valid @RequestBody FindPasswordRequestDto req) {
        userService.findPassword(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비회원 비밀번호 재설정(구현 완료)")
    @PatchMapping("/pwd-nonuser")
    public ResponseEntity<?> changePasswordNonUser(
        @Valid @RequestBody NonUserChangePasswordRequestDto req) {
        userService.changeNonUserPassword(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 비밀번호 재설정(구현 완료)")
    @PatchMapping("/pwd-user")
    public ResponseEntity<?> changePasswordUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody UserChangePasswordRequestDto req) {
        userService.changeUserPassword(userDetails.getUser(), req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth")
    public ResponseEntity<?> phoneAuth(@Valid @RequestBody PhoneAuthRequestDto req) {
        return ResponseEntity.ok().body(smsService.sendVerificationMessage(req.getPhone()));
    }

    @Operation(summary = "회원가입(구현 완료)")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        try {
            userService.signup(requestDto);
        } catch (UserErrorException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "로그아웃(구현 완료)")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        HttpServletRequest request) {

        String accessToken = jwtUtil.getJwtFromHeader(request);
        userService.logout(userDetails.getUserId(), accessToken);

        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "회원 탈퇴(추가 수정 필요)", description = "개인정보 삭제 범위, 재가입 불가 구분용 정보 필요")
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.deleteUser(userDetails);
        return ResponseEntity.ok().body(null);
    }

    @Operation(summary = "자동로그인(구현 완료)")
    @PostMapping("/relogin")
    public ResponseEntity<?> reLogin(@Valid @RequestBody UserReLoginRequestDto requestDto) {
        return ResponseEntity.ok().body(userService.reLogin(requestDto.getRefresh()));
    }

    @Operation(summary = "access Token 재발급(구현 완료)")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Valid @RequestBody UserReissueRequestDto requestDto) {
        return ResponseEntity.ok().body(userService.reissue(requestDto));
    }
}
