package dutchiepay.backend.domain.user.controller;

import dutchiepay.backend.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        User testUser = User.builder()
                .name("테스트유저")
                .build();

        return ResponseEntity.ok().body(testUser);
    }

}
