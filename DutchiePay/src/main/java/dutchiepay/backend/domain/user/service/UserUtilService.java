package dutchiepay.backend.domain.user.service;

import dutchiepay.backend.domain.user.NicknamePrefix;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUtilService {
    private final UserRepository userRepository;

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));
    }

    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone).orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));
    }

    public String maskEmail(String email) {
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

    public String makeRandomNickname(Long userId) {
        NicknamePrefix prefix = NicknamePrefix.of(userId);

        return String.format("%s %s%s", prefix.getPrefix(), "더취", (userId / 8));
    }
}
