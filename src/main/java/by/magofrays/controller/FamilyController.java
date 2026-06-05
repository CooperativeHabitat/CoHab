package by.magofrays.controller;

import by.magofrays.dto.request.CreateInvitationRequest;
import by.magofrays.dto.request.CreateUpdateFamilyRequest;
import by.magofrays.dto.request.InvitationRequest;
import by.magofrays.dto.response.AccessResponse;
import by.magofrays.dto.response.ReadFamilyDto;
import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.entity.Access;
import by.magofrays.entity.Invitation;
import by.magofrays.security.MemberPrincipal;
import by.magofrays.service.AccessService;
import by.magofrays.service.FamilyService;
import by.magofrays.validation.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/family")
public class FamilyController {
    private final FamilyService familyService;
    private final AccessService accessService;

    @GetMapping("{familyId}/members")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#familyId, 'family', 'SHOW_MEMBERS')")
    public ResponseEntity<List<ReadFamilyMemberDto>> getFamilyMembers(@PathVariable UUID familyId) {
        return ResponseEntity.ok(familyService.getFamilyMembersByMemberId(familyId));
    }

    @GetMapping("/{familyId}/accesses")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<AccessResponse>> getMemberAccesses(
            @PathVariable UUID familyId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        UUID memberId = principal.getId();
        return ResponseEntity.ok(
                accessService.getAccessesByFamilyAndMemberId(familyId, memberId).stream()
                        .map(Access::valueOf)
                        .map(access -> new AccessResponse(access.name(), access.getDescription()))
                        .toList()
        );
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReadFamilyMemberDto> createFamily(@AuthenticationPrincipal MemberPrincipal principal,
                                                            @RequestBody @Validated CreateUpdateFamilyRequest request) {
        UUID memberId = principal.getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(familyService.createFamily(request.familyName(), memberId));
    }

    @PostMapping("/create-invitation")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'GENERATE_INVITE_LINK')")
    public ResponseEntity<Invitation> createInvitation(
            @RequestBody @Validated CreateInvitationRequest request,
            @AuthenticationPrincipal MemberPrincipal principal) {
        UUID memberId = principal.getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(familyService.createInvitation(request, memberId));
    }

    @PostMapping("/use-invitation")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReadFamilyMemberDto> getIntoFamilyByInvitation(
            @RequestBody @Validated InvitationRequest invitationCode,
            @AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity.ok(familyService.getIntoFamilyByInvitation(invitationCode.code(), principal.getId()));
    }


    @PutMapping
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'RENAME_FAMILY')")
    public ResponseEntity<ReadFamilyMemberDto> update(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody @Validated({UpdateGroup.class}) CreateUpdateFamilyRequest request
    ) {
        UUID memberId = principal.getId();
        return ResponseEntity.ok(familyService.updateFamily(request, memberId));
    }

    @DeleteMapping("/{familyId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> delete(
            @PathVariable @NotNull UUID familyId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        var memberId = principal.getId();
        familyService.deleteFamily(familyId, memberId);
        return ResponseEntity.noContent().build();
    }
}
