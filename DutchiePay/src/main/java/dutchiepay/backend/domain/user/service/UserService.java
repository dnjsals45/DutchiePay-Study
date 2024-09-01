package dutchiepay.backend.domain.user.service;

import dutchiepay.backend.domain.user.dto.UserSignupRequestDto;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserSignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        existsNickname(nickname);

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        userRepository.save(new User(
            requestDto.getEmail(),
            requestDto.getName(),
            requestDto.getPhone(),
            encodedPassword,
            nickname,
            requestDto.getLocation()
        ));
    }

    public void existsNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
    }
}
