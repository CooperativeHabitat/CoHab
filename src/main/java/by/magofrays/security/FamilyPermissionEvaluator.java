package by.magofrays.security;

import by.magofrays.exception.BusinessException;
import by.magofrays.repository.FamilyRepository;
import by.magofrays.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FamilyPermissionEvaluator implements PermissionEvaluator {
    private final AccessService accessService;
    private final FamilyRepository familyRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false; //todo
    }

    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (!authentication.isAuthenticated()) {
            return false;
        }
        if (Objects.equals(targetType, "family")) {
            var jwt = (Jwt) authentication.getPrincipal();
            if(jwt == null) {
                return false;
            }
            var subject = jwt.getSubject();
            if(subject == null) {
                return false;
            }
            var memberId = UUID.fromString(subject);
            var familyId = UUID.fromString(targetId.toString());
            var family = familyRepository.findById(familyId).orElseThrow(
                    () -> new BusinessException(HttpStatus.NOT_FOUND,
                            "Семьи %s не существует".formatted(familyId))
            );

            var accesses = accessService
                    .getAccessesByFamilyAndMemberId(UUID.fromString(targetId.toString()), memberId); // получаем из бд
            if (accesses == null) {
                return false;
            }
            return family
                    .getCreatedBy()
                    .getId()
                    .equals(memberId) ||
                    accesses
                            .stream()
                            .anyMatch(access -> access.equals(permission));
        }
        return false;
    }
}
