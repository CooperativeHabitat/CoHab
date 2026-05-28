package by.magofrays.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtDecoder {
    private final JwtProperties properties;

    public DecodedJWT decode(String token) {
        return JWT
                .require(Algorithm.RSA256(properties.getPublicKey()))
                .build()
                .verify(token);
    }

}
