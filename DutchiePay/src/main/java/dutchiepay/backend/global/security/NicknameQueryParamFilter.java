package dutchiepay.backend.global.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/*
    SecurityFilterChain 의 requestMatcher 는 쿼리 파라미터 지원 X
    닉네임 중복 확인 시 url 에 파라미터 들어감
    이를 확인하기 위한 필터
*/
public class NicknameQueryParamFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {

        // /user 경로인지 확인
        String requestURI = request.getRequestURI();

        // nickname 파라미터가 있는지 확인
        String nickname = request.getParameter("nickname");

        if ("/users".equals(requestURI) && nickname != null && !nickname.isEmpty()) {
            return nickname;
        }
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "";
    }

}
