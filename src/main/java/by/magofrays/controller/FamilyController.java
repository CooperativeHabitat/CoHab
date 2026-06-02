package by.magofrays.controller;

import by.magofrays.dto.request.CreateInvitationRequest;
import by.magofrays.dto.request.InvitationRequest;
import by.magofrays.dto.request.UpdateFamilyRequest;
import by.magofrays.dto.response.ReadFamilyDto;
import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.entity.Invitation;
import by.magofrays.security.MemberPrincipal;
import by.magofrays.service.FamilyService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ReadFamilyMemberDto>> getFamilyMembers(@PathVariable UUID familyId) {
        return ResponseEntity.ok(familyService.getFamilyMembersByMemberId(familyId));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReadFamilyMemberDto> createFamily(@AuthenticationPrincipal MemberPrincipal principal,
                                            @RequestBody @Validated @NotBlank String familyName) {
        UUID memberId = principal.getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(familyService.createFamily(familyName, memberId));
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


    @PutMapping("/change-name")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'RENAME_FAMILY')")
    public ResponseEntity<ReadFamilyDto> update(@RequestBody @Validated UpdateFamilyRequest request) {
        return null;
    }
}
