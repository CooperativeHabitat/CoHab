package by.magofrays.service;

import by.magofrays.dto.request.LoginRequest;
import by.magofrays.dto.request.RegistrationRequest;
import by.magofrays.dto.response.TokenResponse;
import by.magofrays.entity.Member;
import by.magofrays.exception.BusinessException;
import by.magofrays.repository.MemberRepository;
import by.magofrays.security.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public TokenResponse signIn(LoginRequest request) {
        var member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Профиль не найден!"));
        if (request.password().isBlank() || !BCrypt.checkpw(request.password(),
                member.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Неккоректный логин или пароль!");
        }
        Instant now = Instant.now();
        var accessToken = generateAccessToken(member, now);
        var refreshToken = generateRefreshToken(member, now);
        return new TokenResponse(
                accessToken,
                refreshToken
        );
    }

    public TokenResponse signUp(RegistrationRequest request) {
        var member = memberService.createMember(request);
        Instant now = Instant.now();
        var accessToken = generateAccessToken(member, now);
        var refreshToken = generateRefreshToken(member, now);
        return new TokenResponse(
                accessToken,
                refreshToken
        );
    }


    private String generateAccessToken(Member member, Instant now) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cohab-family")
                .subject(member.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.accessExpiresAt()))
                .id(member.getId().toString())
                .claim("role", member.getSuperRole())
                .claim("username", member.getUsername())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String generateRefreshToken(Member member, Instant now) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cohab-family")
                .subject(member.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.refreshExpiresAt()))
                .claim("type", "refresh")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
