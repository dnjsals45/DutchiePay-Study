package dutchiepay.backend.domain.user.service;

import dutchiepay.backend.domain.user.dto.FindEmailRequestDto;
import dutchiepay.backend.domain.user.dto.FindEmailResponseDto;
import dutchiepay.backend.domain.user.dto.FindPasswordRequestDto;
import dutchiepay.backend.domain.user.dto.NonUserChangePasswordRequestDto;
import dutchiepay.backend.domain.user.dto.UserChangePasswordRequestDto;
import dutchiepay.backend.domain.user.dto.UserLoginResponseDto;
import dutchiepay.backend.domain.user.dto.UserReLoginResponseDto;
import dutchiepay.backend.domain.user.dto.UserReissueRequestDto;
import dutchiepay.backend.domain.user.dto.UserReissueResponseDto;
import dutchiepay.backend.domain.user.dto.UserSignupRequestDto;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.jwt.JwtUtil;
import dutchiepay.backend.global.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserUtilService userUtilService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2AuthorizedClientService oauthService;
    private final AccessTokenBlackListService accessTokenBlackListService;
    private final JwtUtil jwtUtil;

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
    public void logout(Long userId, String accessToken) {
        User user = userUtilService.findById(userId);

        accessTokenBlackListService.addBlackList(accessToken);
        user.deleteRefreshToken();
        userRepository.save(user);
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
                throw new UserErrorException(UserErrorCode.USER_TERMINATED);
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
            throw new UserErrorException(UserErrorCode.USER_TERMINATED);
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

    @Transactional
    public void unlinkKakao(UserDetailsImpl userDetails) {
        OAuth2AuthorizedClient authorizedClient = oauthService.loadAuthorizedClient(
            "kakao", // OAuth2 로그인 제공자 이름 (예: "google", "naver")
            userDetails.getUsername() // 현재 인증된 사용자
        );
        String kakaoAccess = null;
        if (authorizedClient != null) {
            kakaoAccess = authorizedClient.getAccessToken().getTokenValue();// Access Token 추출
        }
        RestTemplate restTemplate = new RestTemplate();

        // POST 요청으로 데이터 전송
        // HttpHeaders 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccess); // Authorization 헤더 설정

        // HttpEntity에 본문 없이 헤더만 담기
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // POST 요청 보내기 (request body 없음)
        restTemplate.exchange(
            "https://kapi.kakao.com/v1/user/unlink",  // 요청할 URL
            HttpMethod.POST,                 // HTTP 메서드
            entity,                          // HttpEntity (본문 없음, 헤더만 있음)
            String.class                     // 응답 타입
        );
    }

    @Transactional
    public void unlinkNaver(UserDetailsImpl userDetails) {

        OAuth2AuthorizedClient authorizedClient = oauthService.loadAuthorizedClient(
            "naver", // OAuth2 로그인 제공자 이름 (예: "google", "naver")
            userDetails.getUsername() // 현재 인증된 사용자
        );

        String naverAccess = null;
        if (authorizedClient != null) {
            naverAccess = authorizedClient.getAccessToken().getTokenValue();// Access Token 추출
        }
        RestTemplate restTemplate = new RestTemplate();

        // POST 요청으로 데이터 전송
        String data = "?client_id=" + naverClientId +
            "&client_secret=" + naverClientSecret +
            "&access_token=" + naverAccess +
            "&service_provider=NAVER" +
            "&grant_type=delete";

        restTemplate.exchange(
            "https://nid.naver.com/oauth2.0/token" + data,  // 요청할 URL
            HttpMethod.POST,                 // HTTP 메서드
            null,                          // HttpEntity
            String.class                     // 응답 타입
        );
    }

    @Transactional
    public void deleteOauthUser(UserDetailsImpl userDetails) {
        userRepository.findByOauthProviderAndEmail(userDetails.getOAuthProvider(), userDetails.getEmail())
                .orElseThrow(() -> new UserErrorException(UserErrorCode.USER_EMAIL_NOT_FOUND)).delete();
    }

    @Transactional
    public void deleteUser(UserDetailsImpl userDetails) {
        userRepository.findByEmailAndOauthProviderIsNull(userDetails.getEmail())
            .orElseThrow(() -> new UserErrorException(UserErrorCode.USER_EMAIL_NOT_FOUND)).delete();
    }

    public UserLoginResponseDto userInfo(UserDetailsImpl userDetails) {
        User user = userRepository.findByOauthProviderAndEmail(userDetails.getOAuthProvider(),
                userDetails.getEmail())
            .orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));

        return UserLoginResponseDto.toDto(user, reissueAccessToken(user.getUserId()));
    }

    public UserReLoginResponseDto reLogin(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new UserErrorException(UserErrorCode.INVALID_REFRESH_TOKEN));

        if (user.getPhone() == null) {
            return UserReLoginResponseDto.toDto(user, reissueAccessToken(user.getUserId()), false);
        }
        return UserReLoginResponseDto.toDto(user, reissueAccessToken(user.getUserId()), true);
    }

    public String reissueAccessToken(Long userId) {
        return jwtUtil.createAccessToken(userId);
    }

    @Transactional
    public UserReissueResponseDto reissue(UserReissueRequestDto requestDto) {
        String accessToken = requestDto.getAccess();
        if (!accessTokenBlackListService.isTokenBlackListed(accessToken)) {
            accessTokenBlackListService.addBlackList(accessToken);
        }

        User user = userRepository.findByRefreshToken(requestDto.getRefresh())
            .orElseThrow(() -> new UserErrorException(UserErrorCode.INVALID_REFRESH_TOKEN));

        return UserReissueResponseDto.toDto(reissueAccessToken(user.getUserId()));
    }


    @Transactional
    public String checkToken(String accessToken, String refreshToken) {

        if (accessToken != null && !accessToken.trim().isEmpty()) {

            try {
                if (accessTokenBlackListService.isTokenBlackListed(accessToken)) {
                    throw new UserErrorException(UserErrorCode.INVALID_ACCESS_TOKEN);
                }

                final JwtUtil jwtUtil = new JwtUtil();
                Claims claims = jwtUtil.getUserInfoFromToken(accessToken);

                return accessToken;
            } catch (ExpiredJwtException | UserErrorException e) {
                if (e instanceof ExpiredJwtException) {
                    accessTokenBlackListService.addBlackList(accessToken);
                }
                if (refreshToken == null && refreshToken.trim().isEmpty()) {
                    throw new UserErrorException(UserErrorCode.INVALID_ACCESS_TOKEN);
                }
            }

        } else if (refreshToken != null && !refreshToken.trim().isEmpty()) {

            final JwtUtil jwtUtil = new JwtUtil();
            Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
            Long userId = claims.get("userId", Long.class);

            User user = userRepository.findById(userId).orElseThrow(
                () -> new UserErrorException(UserErrorCode.USER_NOT_FOUND)
            );

            if (refreshToken.equals(user.getRefreshToken())) {
                return jwtUtil.createAccessToken(userId);
            } else {
                throw new UserErrorException(UserErrorCode.INVALID_REFRESH_TOKEN);
            }

        }
        throw new IllegalArgumentException("액세스 토큰 또는 리프레시 토큰이 제공되어야 합니다.");
    }
}
