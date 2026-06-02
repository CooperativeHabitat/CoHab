package by.magofrays.service;

import by.magofrays.configuration.FamilyProperties;
import by.magofrays.dto.request.AttachRoleRequest;
import by.magofrays.dto.request.CreateUpdateRoleRequest;
import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.dto.response.RoleDto;
import by.magofrays.entity.Access;
import by.magofrays.entity.Family;
import by.magofrays.entity.Role;
import by.magofrays.exception.BusinessException;
import by.magofrays.mapper.MemberMapper;
import by.magofrays.mapper.RoleMapper;
import by.magofrays.repository.FamilyMemberRepository;
import by.magofrays.repository.FamilyRepository;
import by.magofrays.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleMapper roleMapper;
    private final FamilyRepository familyRepository;
    private final RoleRepository roleRepository;
    private final NotificationService notificationService;
    private final FamilyProperties familyProperties;
    private final AccessService accessService;
    private final FamilyMemberRepository familyMemberRepository;
    private final MemberMapper familyMemberMapper;

    @Transactional
    public List<Role> createBaseRoles(Family family) {
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
                                Access.SHOW_TASKS,
                                Access.SHOW_MEMBERS,
                                Access.GENERATE_INVITE_LINK,
                                Access.SHOW_CHAT,
                                Access.CREATE_MESSAGE,
                                Access.REACT_MESSAGE,
                                Access.SHOW_ROLES
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


    @Transactional
    public RoleDto createRole(CreateUpdateRoleRequest request) {
        log.debug("Trying to create role {} for family {}", request.roleName(), request.familyId());
        var family = familyRepository.findById(request.familyId())
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND, "Семьи с id " + request.familyId() + " не существует"));
        if (roleRepository.findByNameAndFamily_Id(request.roleName(), request.familyId()).isPresent()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Роль в семье %s с таким названием уже существует".formatted(request.familyId()));
        }
        var entity = roleMapper.toEntity(request);
        entity.setFamily(family);
        entity = roleRepository.save(entity);
        notificationService.sendNotificationFamily("create-role",
                "Роль %s была создана".formatted(entity.getName()),
                getClass().getName(),
                family,
                null
        );
        log.info("Created role {} for family {}", request.roleName(), request.familyId());
        return roleMapper.toDto(entity);
    }

    @Transactional
    public RoleDto updateRole(CreateUpdateRoleRequest request) {
        log.debug("Trying to update role {} for family {}", request.roleName(), request.familyId());
        var entity = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                        "Роль с id " + request.roleId() + " не существует"));
        entity.setName(request.roleName());
        entity.setAccessList(request.accesses().stream().map(Enum::name).toList());
        entity.setValue(request.value());
        entity = roleRepository.save(entity);
        log.info("Role {} was updated", request.roleId());
        return roleMapper.toDto(entity);
    }

    public List<RoleDto> getFamilyRoles(UUID familyId) {
        log.info("Sending roles in family {}", familyId);
        return roleRepository.findByFamily_Id(familyId).stream().map(roleMapper::toDto).toList();
    }

    @Transactional
    public ReadFamilyMemberDto attachRoleToMember(AttachRoleRequest request){
        log.debug("Trying to attach role '{}' to familyMember {}", request.roleName(), request.familyId());
        var familyMemberEntity = familyMemberRepository.findById(request.familyMemberId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                        "Члена семьи с id: " + request.familyMemberId() + " не существует"
                        ));
        var roleEntity = roleRepository.findByNameAndFamily_Id(request.roleName(), request.familyId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                        "Роли %s не существует в семье %s".formatted(request.roleName(), request.familyId())));

        if(familyMemberEntity.getRoles().contains(roleEntity)){
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Роль '%s' уже назначена на члена семьи: %s".formatted(
                            request.roleName(),
                            request.familyMemberId()));
        }
        roleEntity.addFamilyMember(familyMemberEntity);
        familyMemberEntity.addRole(roleEntity);
        familyMemberRepository.saveAndFlush(familyMemberEntity);
        accessService.updateAccesses(request.familyId(), familyMemberEntity.getMember().getId());
        log.info("Role '{}' attached to familyMember: {}", request.roleName(), request.familyMemberId());
        notificationService.sendNotificationFamily("create-role",
                "Роль '%s' была назначена на %s".formatted(request.roleName(), familyMemberEntity.getMember().getUsername()),
                getClass().getName(),
                familyMemberEntity.getFamily(),
                null
        );
        return familyMemberMapper.toDto(familyMemberEntity);
    }

    @Transactional
    public ReadFamilyMemberDto detachRoleFromMember(AttachRoleRequest request){
        log.debug("Trying to detach role '{}' to familyMember {}", request.roleName(), request.familyId());
        var familyMemberEntity = familyMemberRepository.findById(request.familyMemberId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                        "Члена семьи с id: " + request.familyMemberId() + " не существует"
                ));
        var roleEntity = roleRepository.findByNameAndFamily_Id(request.roleName(), request.familyId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                        "Роли %s не существует в семье %s".formatted(request.roleName(), request.familyId())));
        if(!familyMemberEntity.getRoles().contains(roleEntity)){
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Роль '%s' не назначена на члена семьи: %s".formatted(
                            request.roleName(),
                            request.familyMemberId()));
        }
        roleEntity.removeFamilyMember(familyMemberEntity);
        familyMemberEntity.getRoles().remove(roleEntity);
        familyMemberRepository.saveAndFlush(familyMemberEntity);
        accessService.updateAccesses(request.familyId(), familyMemberEntity.getMember().getId());
        log.info("Role '{}' detached from familyMember: {}", request.roleName(), request.familyMemberId());
        notificationService.sendNotificationFamily("create-role",
                "Роль '%s' была отозвана на %s".formatted(request.roleName(), familyMemberEntity.getMember().getUsername()),
                getClass().getName(),
                familyMemberEntity.getFamily(),
                null
        );
        return familyMemberMapper.toDto(familyMemberEntity);
    }


}
