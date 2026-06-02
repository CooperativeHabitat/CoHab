package by.magofrays.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied() {
        log.info("Sending access denied error");
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
        "Проверьте наличие соответствующих прав для данной семьи"
        );
        problem.setTitle("AccessDeniedException");
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials() {
        log.info("Sending bad credentials error");
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Неверное имя пользователя или пароль"
        );
        problem.setTitle("BadCredentialsException");
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
}
