package by.magofrays.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final ObjectMapper objectMapper;
    private final JwtProperties properties;

    @SneakyThrows
    public String issue(MemberPrincipal principal) {
        List<String> authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return JWT.create()
                .withSubject(String.valueOf(principal.getId()))
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(Duration.of(properties.getExpiresAt(), ChronoUnit.SECONDS)))
                .withClaim("username", principal.getUsername())
                .withClaim("superRole", authorities.getFirst())
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }


}
