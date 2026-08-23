package by.magofrays.controller;

import by.magofrays.dto.request.UpdatePersonalInfoRequest;
import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.dto.response.ReadMemberDto;
import by.magofrays.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReadMemberDto> findByUsername(
            @AuthenticationPrincipal Jwt principal
    ) {
        return ResponseEntity.ok(memberService.findById(UUID.fromString(principal.getId())));
    }

    @GetMapping("/families")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<ReadFamilyMemberDto>> getFamilies(
            @AuthenticationPrincipal Jwt principal
    ) {
        return ResponseEntity.ok(memberService.getFamilyMembers(
                UUID.fromString(principal.getId())));
    }

    @GetMapping("/hasFamily")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> hasFamily(
            @AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.ok(
                memberService.memberHasFamily(UUID.fromString(principal.getId())));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReadMemberDto> update(UpdatePersonalInfoRequest request) {
        return ResponseEntity.ok(memberService.updatePersonalInfo(request));
    }

}
