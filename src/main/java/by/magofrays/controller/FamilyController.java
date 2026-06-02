package by.magofrays.controller;

import by.magofrays.dto.request.CreateInvitationRequest;
import by.magofrays.dto.request.CreateRoleRequest;
import by.magofrays.dto.request.InvitationRequest;
import by.magofrays.dto.request.UpdateFamilyRequest;
import by.magofrays.dto.response.ReadFamilyDto;
import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.dto.response.RoleDto;
import by.magofrays.entity.Access;
import by.magofrays.entity.Invitation;
import by.magofrays.security.MemberPrincipal;
import by.magofrays.service.FamilyService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/family")
public class FamilyController {
    private final FamilyService familyService;

    @GetMapping("{familyId}/members")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#familyId, 'family', 'SHOW_MEMBERS')")
    public List<ReadFamilyMemberDto> getFamilyMembers(@PathVariable UUID familyId) {
        return familyService.getFamilyMembersByMemberId(familyId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')")
    public ReadFamilyMemberDto createFamily(@AuthenticationPrincipal MemberPrincipal principal,
                                            @RequestBody @Validated @NotBlank String familyName) {
        UUID memberId = principal.getId();
        return familyService.createFamily(familyName, memberId);
    }

    @PostMapping("/create-invitation")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'GENERATE_INVITE_LINK')")
    public Invitation createInvitation(@RequestBody @Validated CreateInvitationRequest request,
                                       @AuthenticationPrincipal MemberPrincipal principal) {
        UUID memberId = principal.getId();
        return familyService.createInvitation(request, memberId);
    }

    @PostMapping("/use-invitation")
    @PreAuthorize("hasAuthority('USER')")
    public ReadFamilyMemberDto getIntoFamilyByInvitation(@RequestBody InvitationRequest invitationCode,
                                                         @AuthenticationPrincipal MemberPrincipal principal) {
        return familyService.getIntoFamilyByInvitation(invitationCode.code(), principal.getId());
    }


    @PutMapping("/change-name")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'UPDATE_FAMILY')")
    public ReadFamilyDto update(@RequestBody @Validated UpdateFamilyRequest request) {
        return null;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#familyId, 'family', 'SHOW_ROLES')")
    public List<RoleDto> getFamilyRoles(@NotNull UUID familyId){
        return familyService.getFamilyRoles(familyId);
    }

    @GetMapping("/accesses")
    @PreAuthorize("hasAuthority('USER')")
    public List<Access> getAccesses(){
        return List.of(Access.values());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'CREATE_ROLE')")
    public RoleDto createRole(@RequestBody @Validated CreateRoleRequest createRoleRequest){
        return familyService.createRole(createRoleRequest);
    }
}
