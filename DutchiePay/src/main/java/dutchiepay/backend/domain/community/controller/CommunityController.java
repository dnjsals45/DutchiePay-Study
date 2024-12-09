package dutchiepay.backend.domain.community.controller;

import dutchiepay.backend.domain.community.dto.ChangeStatusRequestDto;
import dutchiepay.backend.domain.community.service.MartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
    private final MartService martService;

    @PatchMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeStatus(@RequestBody ChangeStatusRequestDto req) {
        if (req.getCategory().equals("마트/배달")) {
            martService.changeStatus(req);
        }
        return ResponseEntity.ok().build();
    }
}
