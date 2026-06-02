package by.magofrays.controller;

import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.dto.response.ReadMemberDto;
import by.magofrays.exception.BusinessException;
import by.magofrays.security.MemberPrincipal;
import by.magofrays.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReadMemberDto> findByUsername(@AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity.ok(memberService.findByUsername(principal.getUsername())
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND, "Пользователь с никнеймом: " + principal.getUsername() + " не существует.")));
    }

    @GetMapping("/families")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<ReadFamilyMemberDto>> getFamilies(@AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity.ok(memberService.getFamilyMembers(principal.getId()));
    }

    @GetMapping("/hasFamily")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> hasFamily(@AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity.ok(memberService.memberHasFamily(principal.getId()));
    }

}
