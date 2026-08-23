package by.magofrays.controller;

import by.magofrays.dto.request.LoginRequest;
import by.magofrays.dto.request.RegistrationRequest;
import by.magofrays.dto.response.TokenResponse;
import by.magofrays.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final SecurityService securityService;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/signIn")
    public ResponseEntity<TokenResponse> signIn(
            @RequestBody @Validated LoginRequest request) {
        var token = securityService.signIn(request);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/signUp")
    public ResponseEntity<TokenResponse> signUp(@RequestBody @Validated RegistrationRequest registrationRequest) {
        var token = securityService.signUp(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(token);
    }

    @PostMapping("/isAuthenticated")
    public void isAuthenticated() {
    }

    @PreAuthorize("hasAnyAuthority('USER', 'GOD')")
    @PostMapping("/isUser")
    public void isUser() {
    }


}
