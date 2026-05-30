package by.magofrays.security;

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

@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied() {
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
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Неверное имя пользователя или пароль"
        );
        problem.setTitle("BadCredentialsException");
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabled() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Ваш аккаунт отключён. Обратитесь в поддержку."
        );
        problem.setTitle("DisabledException");
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    @ExceptionHandler(LockedException.class)
    public ProblemDetail handleLocked() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Аккаунт временно заблокирован из-за множества неудачных попыток входа."
        );
        problem.setTitle("LockedException");
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
}
