package dutchiepay.backend.domain.user.service;

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
}
