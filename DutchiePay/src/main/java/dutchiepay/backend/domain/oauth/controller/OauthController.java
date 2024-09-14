package dutchiepay.backend.domain.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OauthController {
    @Operation(summary = "소셜 로그인(구현중)")
    @GetMapping("/oauth/signup")
    public String signup(@RequestParam String type) {

        System.out.println("Type: " + type);
        System.out.println("Redirecting..." );
        return "redirect:/oauth2/authorization/" + type;
    }

}
