package dutchiepay.backend.domain.user.service;

import dutchiepay.backend.domain.notice.service.NoticeUtilService;
import dutchiepay.backend.domain.user.dto.FindEmailRequestDto;
import dutchiepay.backend.domain.user.dto.FindEmailResponseDto;
import dutchiepay.backend.domain.user.dto.FindPasswordRequestDto;
import dutchiepay.backend.domain.user.dto.NonUserChangePasswordRequestDto;
import dutchiepay.backend.domain.user.dto.UserChangePasswordRequestDto;
import dutchiepay.backend.domain.user.dto.UserReLoginResponseDto;
import dutchiepay.backend.domain.user.dto.UserReissueRequestDto;
import dutchiepay.backend.domain.user.dto.UserReissueResponseDto;
import dutchiepay.backend.domain.user.dto.UserSignupRequestDto;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.redis.RedisService;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.security.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserUtilService userUtilService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2AuthorizedClientService oauthService;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final NoticeUtilService noticeUtilService;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Transactional
    public void signup(UserSignupRequestDto requestDto) {
        existsNickname(requestDto.getNickname());
        existsPhone(requestDto.getPhone());
        existsEmail(requestDto.getEmail());

        User user = User.builder()
            .email(requestDto.getEmail())
            .password(passwordEncoder.encode(requestDto.getPassword()))
            .phone(requestDto.getPhone())
            .nickname(requestDto.getNickname())
            .location(requestDto.getLocation())
            .build();

        userRepository.save(user);
    }

    @Transactional
    public void logout(Long userId, HttpServletRequest request) {
        redisService.addBlackList(userId, jwtUtil.getJwtFromHeader(request));
        redisService.deleteRefreshToken(redisService.getRefreshToken(userId));
    }

    public void existsNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserErrorException(UserErrorCode.USER_NICKNAME_ALREADY_EXISTS);
        }
    }

    public void existsEmail(String email) {
        Optional<User> user = userRepository.findByEmailAndOauthProviderIsNull(email);

        if (user.isPresent()) {
            User now = user.get();
            if (now.getState() == 1) {
                throw new UserErrorException(UserErrorCode.USER_SUSPENDED);
            } else if (now.getState() == 2) {
                throw new UserErrorException(UserErrorCode.USER_NOT_FOUND);
            } else {
                throw new UserErrorException(UserErrorCode.USER_EMAIL_ALREADY_EXISTS);
            }
        }
    }

    public void existsPhone(String phone) {
        if (userRepository.existsByPhoneAndOauthProviderIsNull(phone)) {
            throw new UserErrorException(UserErrorCode.USER_PHONE_ALREADY_EXISTS);
        }
    }

    public FindEmailResponseDto findEmail(FindEmailRequestDto req) {
        User user = userUtilService.commonUserFindByPhone(req.getPhone());

        if (user.getState() == 1) {
            throw new UserErrorException(UserErrorCode.USER_SUSPENDED);
        } else if (user.getState() == 2) {
            throw new UserErrorException(UserErrorCode.USER_NOT_FOUND);
        }

        return FindEmailResponseDto.of(userUtilService.maskEmail(user.getEmail()));
    }

    public void findPassword(FindPasswordRequestDto req) {
        userUtilService.commonUserFindByEmailAndPhone(req.getEmail(), req.getPhone());
    }

    @Transactional
    public void changeNonUserPassword(NonUserChangePasswordRequestDto req) {
        User user = userUtilService.commonUserFindByEmail(req.getEmail());

        if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new UserErrorException(UserErrorCode.USER_SAME_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void changeUserPassword(User user, UserChangePasswordRequestDto req) {
        if (req.getPassword().equals(req.getNewPassword())) {
            throw new UserErrorException(UserErrorCode.SAME_PASSWORD);
        }

        if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            user.changePassword(passwordEncoder.encode(req.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new UserErrorException(UserErrorCode.INVALID_PASSWORD);
        }

        userRepository.save(user);
    }

    /**
     * 이메일 회원 탈퇴
     * AT는 BlackList 처리, RT는 삭제 처리
     * @param userDetails
     * @param request
     */
    @Transactional
    public void deleteUser(UserDetailsImpl userDetails, HttpServletRequest request) {
        redisService.addBlackList(userDetails.getUserId(), jwtUtil.getJwtFromHeader(request));
        redisService.deleteRefreshToken(redisService.getRefreshToken(userDetails.getUserId()));
        userRepository.findByEmailAndOauthProviderIsNull(userDetails.getEmail())
            .orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND)).delete();
    }

    public UserReLoginResponseDto reLogin(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null) throw new UserErrorException(UserErrorCode.INVALID_REFRESH_TOKEN);
        
        Long userId = redisService.findUserIdFromRefreshToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));
        Boolean hasNotice = noticeUtilService.existUnreadNotification(user, LocalDateTime.now().minusDays(7));
        return UserReLoginResponseDto.toDto(user, reissueAccessToken(userId), user.getPhone() != null, hasNotice);
    }

    private String reissueAccessToken(Long userId) {
        return jwtUtil.createAccessToken(userId);
    }

    @Transactional
    public UserReissueResponseDto reissue(UserReissueRequestDto requestDto, HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null) throw new UserErrorException(UserErrorCode.INVALID_REFRESH_TOKEN);

        Long userId = redisService.findUserIdFromRefreshToken(refreshToken);

        String accessToken = requestDto.getAccess();
        if (!redisService.isTokenBlackListed(accessToken)) {
            redisService.addBlackList(userId, accessToken);
        }

        return UserReissueResponseDto.toDto(reissueAccessToken(userId));
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        // 쿠키에서 refresh token을 꺼내 전달
        if (cookies != null)
            for (Cookie cookie : cookies)
                if (cookie.getName().equals("refresh"))
                    return cookie.getValue();
        return null;
    }

}
