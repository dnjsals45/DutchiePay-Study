package dutchiepay.backend.domain.user.service;

import dutchiepay.backend.domain.user.dto.FindEmailReq;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author     dnjsals45
 * @version    1.0.0
 * @since      1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    public String findEmail(FindEmailReq req) {
        Users user = userRepository.findByPhone(req.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 유저가 없습니다."));

        return maskEmail(user.getEmail());
    }

    private String maskEmail(String email) {
        int index = email.indexOf("@");

        String id = email.substring(0, index);
        String domain = email.substring(index);

        if (id.length() <= 2) {
            return email;
        }

        StringBuilder result = new StringBuilder();
        result.append(id.charAt(0));

        for (int i = 1; i < id.length() - 1; i++) {
            result.append("*");
        }

        result.append(id.charAt(id.length() - 1));

        return result.append(domain).toString();
    }
}
