package dutchiepay.backend.domain.user.service;

import dutchiepay.backend.domain.user.dto.FindEmailRequestDto;
import dutchiepay.backend.domain.user.dto.FindPasswordRequestDto;
import dutchiepay.backend.domain.user.dto.NonUserChangePasswordRequestDto;
import dutchiepay.backend.domain.user.dto.UserChangePasswordRequestDto;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String findEmail(FindEmailRequestDto req) {
        User user = userRepository.findByPhone(req.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 유저가 없습니다."));

        return maskEmail(user.getEmail());
    }

    public String findPassword(FindPasswordRequestDto req) {
        // TODO findByPhone을 사용할 것인지 findByEmailAndPhone을 사용할 것인지
        User user = userRepository.findByPhone(req.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 유저가 없습니다."));

        // TODO 유저 패스워드 디코딩을 진행해야 함
        return user.getPassword();
    }

    @Transactional
    public void changeNonUserPassword(NonUserChangePasswordRequestDto req) {
        // TODO findByPhone을 사용할 것인지 findByEmailAndPhone을 사용할 것인지
        User user = userRepository.findByPhone(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 유저가 없습니다."));

        user.changePassword(req.getPassword());
    }

    @Transactional
    public String changeUserPassword(UserChangePasswordRequestDto req) {
        // TODO 유저 비밀번호 재설정의 경우에는 토큰으로 유저를 파악해서 진행
        return null;
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
