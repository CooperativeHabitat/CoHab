package by.magofrays.security;

import by.magofrays.entity.Access;
import by.magofrays.entity.SuperRole;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtToPrincipalMapper {

    public MemberPrincipal convert(DecodedJWT jwt) {
        return MemberPrincipal.builder()
                .id(UUID.fromString(jwt.getSubject()))
                .username(jwt.getClaim("username").asString())
                .superRole(SuperRole.valueOf(jwt.getClaim("superRole").asString())).build();
    }

}
