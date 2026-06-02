package by.magofrays.security;

import by.magofrays.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FamilyPermissionEvaluator implements PermissionEvaluator {
    private final AccessService accessService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false; //todo
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (Objects.equals(targetType, "family")) {
            var principal = (MemberPrincipal) authentication.getPrincipal();
            var accesses = accessService
                    .getAccessesByFamilyAndMemberId(UUID.fromString(targetId.toString()), principal.getId()); // получаем из бд
            if (accesses == null) {
                return false;
            }
            return accesses.stream().anyMatch(access -> access.equals(permission));
        }
        return false;
    }
}
