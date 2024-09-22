package dutchiepay.backend.domain.oauth.controller;

import dutchiepay.backend.domain.user.dto.UserLoginResponseDto;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.global.oauth.service.CustomOAuth2UserService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserService userService;

    @Operation(summary = "소셜 로그인(구현중)")
    @GetMapping("/signup")
    public String signup(@RequestParam String type) {

        return "redirect:/oauth2/authorization/" + type;
    }

    @Operation(summary = "소셜 로그인 완료 후 회원 정보 요청받을 controller")
    @PostMapping("/users")
    public ResponseEntity<UserLoginResponseDto> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.userInfo(userDetails));
    }

    @Operation(summary = "소셜 회원 탈퇴(구현중)")
    @DeleteMapping
    public String unlink(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String type){
        if (type.equals("kakao")) {
            userService.unlinkKakao(userDetails);
        } else {
            userService.unlinkNaver(userDetails);
        }

        userService.deleteUser(userDetails);
        return "redirect:/";
    }

}
