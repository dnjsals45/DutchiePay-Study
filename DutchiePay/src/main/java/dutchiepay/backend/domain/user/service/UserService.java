package dutchiepay.backend.domain.user.service;

import dutchiepay.backend.domain.user.dto.*;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserUtilService userUtilService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserSignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        existsNickname(nickname);

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
            .email(requestDto.getEmail())
            .password(encodedPassword)
            .phone(requestDto.getPhone())
            .nickname(requestDto.getNickname())
            .username(requestDto.getName())
            .location(requestDto.getLocation())
            .build();

        userRepository.save(user);
    }

    public void existsNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
    }

    public FindEmailResponseDto findEmail(FindEmailRequestDto req) {
        User user = userUtilService.findByPhone(req.getPhone());

        return FindEmailResponseDto.of(userUtilService.maskEmail(user.getEmail()));
    }

    public void findPassword(FindPasswordRequestDto req) {
        userRepository.findByPhone(req.getPhone())
                .orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void changeNonUserPassword(NonUserChangePasswordRequestDto req) {
        // TODO entity save만으로 PasswordEncoder가 동작하는지 확인 필요
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));

        user.changePassword(req.getPassword());
    }

    @Transactional
    public String changeUserPassword(UserChangePasswordRequestDto req) {
        // TODO 유저 비밀번호 재설정의 경우에는 토큰으로 유저를 파악해서 진행. 추후 구현 필요
        return null;
    }
}
