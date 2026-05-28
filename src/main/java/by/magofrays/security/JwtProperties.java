package by.magofrays.security;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
@Data
@ConfigurationProperties("security.jwt")
public class JwtProperties {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private Integer expiresAt;
}
