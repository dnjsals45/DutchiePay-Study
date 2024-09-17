package dutchiepay.backend.domain.oauth.controller;

import dutchiepay.backend.global.oauth.service.CustomOAuth2UserService;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Operation(summary = "소셜 로그인(구현중)")
    @GetMapping("/signup")
    public String signup(@RequestParam String type) {

        return "redirect:/oauth2/authorization/" + type;
    }

    @Operation(summary = "소셜 회원 탈퇴(구현중)")
    @GetMapping
    public String unlink(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String type){
        if (type.equals("kakao")) {
            customOAuth2UserService.deleteKakaoUser(userDetails);
        } else {
            customOAuth2UserService.deleteNaverUser(userDetails);
        }

        return "redirect:/";
    }

}
