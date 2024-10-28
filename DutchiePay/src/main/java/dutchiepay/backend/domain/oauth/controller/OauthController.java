package dutchiepay.backend.domain.oauth.controller;

import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final UserService userService;

    @Operation(summary = "소셜 로그인(구현 완료)")
    @GetMapping("/signup")
    @PreAuthorize("permitAll()")
    public String signup(@RequestParam String type) {

        return "redirect:/oauth2/authorization/" + type;
    }

    @Operation(summary = "소셜 회원 탈퇴(구현 완료)")
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public String unlink(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String type){
        if (type.equals("kakao")) {
            userService.unlinkKakao(userDetails);
        } else {
            userService.unlinkNaver(userDetails);
        }
        userService.deleteOauthUser(userDetails);
        return "redirect:/";
    }
}
