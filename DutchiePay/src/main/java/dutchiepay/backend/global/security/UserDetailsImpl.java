package dutchiepay.backend.global.security;

import dutchiepay.backend.entity.User;
import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class UserDetailsImpl implements OAuth2User, UserDetails {
    private User user;
    private Map<String, Object> attributes;

    // 소셜 로그인 사용 시 attributes가 있을 수 있음
    public UserDetailsImpl(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 일반 로그인일 때 attributes는 null일 수 있음
    public UserDetailsImpl(User user) {
        this.user = user;
        this.attributes = null;  // 일반 로그인에서는 null
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public Long getUserId(){
        return user.getUserId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;  // 소셜 로그인이 아니면 null이 반환될 수 있음
    }

    @Override
    public String getName() {
        return user.getUserId().toString();
    }

    public String getOAuthProvider() {
        return user.getOauthProvider();
    }

}
