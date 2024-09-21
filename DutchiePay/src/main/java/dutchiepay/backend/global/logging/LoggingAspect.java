package dutchiepay.backend.global.logging;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@Aspect
public class LoggingAspect {
    @Pointcut(value = "execution(* dutchiepay.backend.domain.*.controller.*Controller.*(..)) " +
            "&& !execution(* dutchiepay.backend.domain.oauth.controller.*Controller.*(..))")
    private void logCut() {
    }

    @Before("logCut()")
    public void beforeLogging(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        log.info("================================= Request ==================================");
        log.info("요청 메서드 이름 : {}", method.getName());
        log.debug("요청 메서드 경로 : {}", method.getDeclaringClass() + "." + method.getName());

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Refresh-Token")) { // 프론트에서 어떻게 보내줄지 모르니 나중에 수정
                    log.debug("요청 쿠키 값 : {}", cookie.getName() + "=" + cookie.getValue());
                }
            }
        }

        Object[] args = joinPoint.getArgs();

        if (args.length == 0) {
            log.info("인자가 없는 요청");
        }

        for (Object arg : args) {
            if (arg == null) continue;

            log.info("요청 파라미터 타입 : {} ⇾ 값 : {}", arg.getClass().getSimpleName(), arg);
        }
    }

    @AfterReturning(pointcut = "logCut()", returning = "result")
    public void afterLogging(JoinPoint joinPoint, Object result) {
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        HttpHeaders headers = response.getHeaders();

        log.info("================================= Response =================================");
        log.info("응답 코드 : {}", response.getStatusCode());
        log.info("응답 헤더 : {}", headers);

        for (Map.Entry<String, String> entry : headers.toSingleValueMap().entrySet()) {
            if (entry.getKey().equals(HttpHeaders.SET_COOKIE) || entry.getKey().equals(HttpHeaders.AUTHORIZATION)) {
                log.debug("응답 헤더 : {} ⇾ 값 : {}", entry.getKey(), entry.getValue());
            } else {
                log.info("응답 헤더 : {} ⇾ 값 : {}", entry.getKey(), entry.getValue());
            }
        }

        log.info("응답 내용 : {}", response.getBody());
        log.info("============================================================================");
    }

    @AfterThrowing(pointcut = "logCut()", throwing = "exception")
    public void afterThrowingLogging(JoinPoint joinPoint, Exception exception) {
        log.error("================================= Exception =================================");
        log.error("예외 종류 : {} ⇾ 메시지 : {}", exception.getClass().getSimpleName(), exception.getMessage());
        log.error("=============================================================================");
    }
}
