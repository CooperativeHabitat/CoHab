package by.magofrays.service;

import by.magofrays.configuration.FamilyProperties;
import by.magofrays.configuration.UserProperties;
import by.magofrays.dto.request.CreateInvitationRequest;
import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.dto.response.RoleDto;
import by.magofrays.entity.*;
import by.magofrays.exception.BusinessException;
import by.magofrays.mapper.MemberMapper;
import by.magofrays.repository.FamilyMemberRepository;
import by.magofrays.repository.FamilyRepository;
import by.magofrays.repository.MemberRepository;
import by.magofrays.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final MemberMapper memberMapper;
    private final UserProperties userProperties;
    private final FamilyProperties familyProperties;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final ConcurrentHashMap<String, Invitation> invitationMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> familyInvitationMap = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

    public List<ReadFamilyMemberDto> getFamilyMembersByMemberId(UUID familyId) {
        return familyMemberRepository.findByFamily_Id(familyId).stream().map(memberMapper::toDto).toList();
    }

    public Invitation createInvitation(CreateInvitationRequest request, UUID memberId) {
        log.debug("Member: {} trying create invitation for family: {}", memberId, request.familyId());
        if (familyInvitationMap.containsKey(request.familyId())) {
            log.debug("Invitation for family: {} already exists", request.familyId());
            return invitationMap.get(
                    familyInvitationMap.get(request.familyId())
            );
        }
        String code = RandomStringUtils.secure().nextAlphanumeric(8).toUpperCase();
        var invitation = Invitation.builder()
                .invitationCode(code)
                .numMembers(request.numMembers())
                .expiresAt(
                        request.expiresAt()
                )
                .familyId(request.familyId())
                .build();
        invitationMap.put(code, invitation);
        familyInvitationMap.put(request.familyId(), code);
        log.info("Created invitation for family: {}", request.familyId());
        var familyMember = familyMemberRepository.findByMember_IdAndFamily_Id(memberId, request.familyId())
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND,
                                "Пользователь с id: " + memberId + " не состоит в семье: " + request.familyId()));
        notificationService.sendNotificationFamily("create-invitation",
                "%s создал код-приглашение".formatted(familyMember.getMember().getUsername()),
                this.getClass().getName(),
                familyMember.getFamily(),
                familyMember.getMember().getId());
        return invitation;
    }


    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.HOURS)
    public void cleaningInvitationMap() {
        log.debug("Cleaning invitation map");
        invitationMap.entrySet().iterator().forEachRemaining(entry -> {
            if (entry.getValue().getExpiresAt().isBefore(LocalDateTime.now())) {
                invitationMap.remove(entry.getKey());
                familyInvitationMap.remove(entry.getValue().getFamilyId());
                var family = familyRepository.findById(entry.getValue().getFamilyId());
                if(family.isPresent()){
                    notificationService.sendNotificationFamily("delete-invitation",
                            "Код приглашение в семью был удален",
                            this.getClass().getName(),
                            family.get(),
                            null);
                } else {
                    log.error("Invitation map contained invitation for family {} which is not exists", entry.getValue().getFamilyId());
                }
            }
        });
    }

    @Transactional
    public ReadFamilyMemberDto getIntoFamilyByInvitation(String invitationCode, UUID memberId) {
        log.info("Member: {} using invitation code: {}", memberId, invitationCode);
        var invitation = invitationMap.get(invitationCode);
        if (invitation == null) {
            log.warn("Invitation code: {}, does not exist ", invitationCode);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Кода приглашения: " + invitationCode + " не существует, либо он прекратил свое действие!");
        }
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Invitation code: {} is outdated", invitationCode);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Кода приглашения: " + invitationCode + " не существует, либо он прекратил свое действие!");
        }
        if (familyMemberRepository.memberInFamily(memberId, invitation.getFamilyId())) {
            log.warn("Member: {} is already in family", memberId);
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Пользователь уже состоит в этой семье!");
        }
        var family = familyRepository.findById(invitation.getFamilyId()).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "Такой семьи не существует!")
        );
        var member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "Такого пользователя не существует!"));
        if (invitation.getNumMembers() == 1) {
            invitationMap.remove(invitationCode);
            familyInvitationMap.remove(invitation.getFamilyId());
        } else {
            invitation.setNumMembers(invitation.getNumMembers() - 1);
            invitationMap.put(invitationCode, invitation);
        }
        var familyMember = addMemberToFamily(family, member);
        log.info("Member: {} added in family: {}", memberId, invitation.getFamilyId());
        notificationService.sendNotificationFamily("invite-member",
                "%s вступил в семью по коду-приглашению".formatted(member.getUsername()),
                getClass().getName(),
                family,
                member.getId()
        );
        return memberMapper.toDto(familyMember);
    }

    @Transactional
    public ReadFamilyMemberDto createFamily(String familyName, UUID memberId) {
        log.debug("Member: {} trying create family: {}", memberId, familyName);
        var owner = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "Пользователь с id: " + memberId + " не существует."));
        var family = Family.builder().familyName(familyName).createdBy(owner).build();
             if (owner.getFamilyMembers().size() > userProperties.getMaxFamilies() && owner.getSuperRole().equals(SuperRole.USER)) {
            log.debug("Member: {} can not create family, because has max families", memberId);
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Пользователь не может создать более " + userProperties.getMaxFamilies() + " семей!");
        }
        family = familyRepository.save(family);
        var roles = createBaseRoles(family);
        log.info("Member: {} created family: {}", memberId, family.getId());
        var ownerFamilyMember = addMemberToFamily(family, owner);
        ownerFamilyMember.getRoles().add(roles.getFirst());
        notificationService.sendNotificationFamily("create-family",
                "Семья была создана",
                getClass().getName(),
                family,
                null);
        return memberMapper.toDto(ownerFamilyMember);
    }

    @Transactional
    public FamilyMember addMemberToFamily(Family family, Member member) {
        log.debug("Adding user: {} to family: {}", member.getId(), family.getId());
        var familyMember = FamilyMember.builder()
                .family(family)
                .member(member)
                .build();
        var role = roleRepository.findByNameAndFamily_Id(familyProperties.getUserRoleName(),
                        family.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Отсутствует основная роль: " + familyProperties.getUserRoleName()));
        familyMember.getRoles().add(role);
        member.getFamilyMembers().add(familyMember);
        family.addMember(familyMember);
        return familyMemberRepository.save(familyMember);
    }


    private List<Role> createBaseRoles(Family family) {
        log.debug("Creating base roles for family: {}", family.getId());
        Role admin = roleRepository.save(Role.builder()
                .family(family)
                .name(familyProperties.getAdminRoleName())
                .accessList(
                        Stream.of(
                                Access.values()
                        ).map(Enum::name).toList()
                )
                .value(familyProperties.getAdminRoleValue()).build());
        Role member = roleRepository.save(Role.builder()
                .family(family)
                .name(familyProperties.getUserRoleName())
                .value(familyProperties.getUserRoleValue())
                .accessList(
                        Stream.of(
                                Access.CREATE_TASK,
                                Access.ASSIGN_TASK,
                                Access.SHOW_MEMBERS,
                                Access.GENERATE_INVITE_LINK,
                                Access.SHOW_CHAT,
                                Access.CREATE_MESSAGE,
                                Access.REACT_MESSAGE
                        ).map(Enum::name).toList()
                ).build());
        notificationService.sendNotificationFamily("create-role",
                "Базовые роли %s, %s были созданы".formatted(member.getName(), admin.getName()),
                getClass().getName(),
                family,
                null
        );
        return List.of(admin, member);
    }

    public List<Access> createRole(RoleDto role){
        return null;
    }
}
