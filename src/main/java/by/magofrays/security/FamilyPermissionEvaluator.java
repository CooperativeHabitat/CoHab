package by.magofrays.security;

import by.magofrays.exception.BusinessException;
import by.magofrays.repository.FamilyMemberRepository;
import by.magofrays.repository.FamilyRepository;
import by.magofrays.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
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

    private final FamilyMemberRepository familyMemberRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false; //todo
    }

    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (Objects.equals(targetType, "family")) {
            var principal = (MemberPrincipal) authentication.getPrincipal();
            var memberId = principal.getId();
            var familyId = UUID.fromString(targetId.toString());
            var family = familyRepository.findById(familyId).orElseThrow(
                    () -> new BusinessException(HttpStatus.NOT_FOUND,
                            "Семьи %s не существует".formatted(familyId))
            );
            if(family.getCreatedBy().getId().equals(memberId)){
                return true;
            }
            var accesses = accessService
                    .getAccessesByFamilyAndMemberId(UUID.fromString(targetId.toString()), memberId); // получаем из бд
            if (accesses == null) {
                return false;
            }
            return accesses.stream().anyMatch(access -> access.equals(permission));
        }
        return false;
    }
}
