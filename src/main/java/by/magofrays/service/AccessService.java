package by.magofrays.service;

import by.magofrays.entity.Access;
import by.magofrays.exception.BusinessException;
import by.magofrays.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessService {
    private final FamilyMemberRepository memberRepository;

    @Transactional
    @Cacheable(value = "family:accesses", key = "#familyId + ':' + #memberId")
    public List<String> getAccessesByFamilyAndMemberId(UUID familyId, UUID memberId) {

        var familyMember = memberRepository.findByMember_IdAndFamily_Id(memberId, familyId).orElseThrow(
                () -> new BusinessException(HttpStatus.BAD_REQUEST, "Не удалось найти пользователя " + memberId + " в семье " + familyId + "!")
        );
        return familyMember.getRoles().stream().flatMap(role -> role.getAccessList().stream()).distinct().collect(Collectors.toList());
    }

}
