package by.magofrays.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

@Component
@ConfigurationProperties("security.jwt")
public record JwtProperties(
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey,
        Duration accessExpiresAt,
        Duration refreshExpiresAt
) {
}

