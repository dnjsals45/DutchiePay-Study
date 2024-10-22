package dutchiepay.backend;

import dutchiepay.backend.domain.user.dto.FindEmailRequestDto;
import dutchiepay.backend.domain.user.dto.FindEmailResponseDto;
import dutchiepay.backend.domain.user.exception.UserErrorCode;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import dutchiepay.backend.domain.user.repository.UserRepository;
import dutchiepay.backend.domain.user.service.UserService;
import dutchiepay.backend.domain.user.service.UserUtilService;
import dutchiepay.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Disabled
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserUtilService userUtilService;

    private User mockUser;

    @BeforeEach
    public void setUpUser() {
        mockUser = User.builder()
                .email("test@example.com")
                .username("테스트")
                .phone("01012345678")
                .nickname("테스트 유저")
                .location("서울시 마포구 신수동")
                .state(0)
                .build();
    }

    @Test
    @Disabled
    void 이메일찾기_유저존재() {
        // given
        FindEmailRequestDto req = new FindEmailRequestDto("01012345678");
        when(userUtilService.findByPhone(req.getPhone())).thenReturn(mockUser);
        when(userUtilService.maskEmail(mockUser.getEmail())).thenReturn("t**t@example.com");

        // when
        FindEmailResponseDto result = userService.findEmail(req);

        // then
        assertThat(result.getEmail()).isEqualTo("t**t@example.com");
        verify(userUtilService).findByPhone(req.getPhone());
        verify(userUtilService).maskEmail(mockUser.getEmail());
    }

    @Test
    @Disabled
    void 이메일찾기_유저없음() {
        // given
        FindEmailRequestDto req = new FindEmailRequestDto("01012345678");
        when(userUtilService.findByPhone(req.getPhone())).thenThrow(new UserErrorException(UserErrorCode.USER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> userService.findEmail(req))
                .isInstanceOf(UserErrorException.class)
                .hasMessage("해당하는 유저가 없습니다.");

        verify(userUtilService).findByPhone(req.getPhone());
    }
}
