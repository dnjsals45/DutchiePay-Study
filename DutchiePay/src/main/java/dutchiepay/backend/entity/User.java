package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "Users")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    //이메일
    @Column(nullable = false)
    private String email;

    //성함
    @Column(length = 5)
    private String username;

    //휴대폰 번호
    @Column(nullable = false, length = 11)
    private String phone;

    //비밀번호
    private String password;

    //닉네임
    @Column(nullable = false, length = 8, unique = true)
    private String nickname;

    //지역
    @Column(nullable = false, length = 15)
    private String location;

    //주소
    private String address;

    //상세 주소
    private String detail;

    //리프레쉬 토큰
    @Column(length = 512)
    private String refreshToken;

    //프로필 이미지
    private String profileImg;

    //탈퇴&정지 여부
    //0 : 정상, 1 : 정지, 2 : 탈퇴
    @Column(nullable = false)
    private int state;

    //소셜 ID
    @Column(length = 20)
    private String oauthId;

    //소셜 종류
    @Column(length = 10)
    private String oauthProvider;

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void changeLocation(String location) {
        this.location = location;
    }

    public void changeAddress(String address, String detail) {
        this.address = address;
        this.detail = detail;
    }

    public void changePhone(String phone) {
        this.phone = phone;
    }

    public void createRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
