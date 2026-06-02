package by.magofrays.controller;

import by.magofrays.dto.response.LoginResponse;
import by.magofrays.dto.request.RegistrationRequest;
import by.magofrays.dto.request.LoginRequest;
import by.magofrays.security.JwtDecoder;
import by.magofrays.security.JwtIssuer;
import by.magofrays.security.MemberPrincipal;
import by.magofrays.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtIssuer jwtIssuer;
    private final JwtDecoder jwtDecoder;
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest loginMemberDto) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginMemberDto.username(), loginMemberDto.password())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        var principal = (MemberPrincipal) auth.getPrincipal();
        var token = jwtIssuer.issue(principal);
        Instant expiresAt = jwtDecoder.decode(token).getExpiresAt().toInstant();
        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .expiresAt(expiresAt.toString())
                .build());
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/registration")
    public ResponseEntity<LoginResponse> registration(@RequestBody @Validated RegistrationRequest registrationRequest) {
        var member = memberService.createMember(registrationRequest);
        var principal = MemberPrincipal.builder()
                .id(member.id())
                .username(member.username())
                .superRole(member.superRole())
                .build();
        var token = jwtIssuer.issue(principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LoginResponse.builder().token(token).build());
    }

    @PostMapping("/isAuthenticated")
    public void isAuthenticated() {
    }

    @PreAuthorize("hasAnyAuthority('USER', 'GOD')")
    @PostMapping("/isUser")
    public void isUser() {
    }


}
