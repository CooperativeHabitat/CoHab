package by.magofrays.controller;

import by.magofrays.dto.request.DeleteRoleRequest;
import by.magofrays.dto.request.AttachRoleRequest;
import by.magofrays.dto.request.CreateUpdateRoleRequest;
import by.magofrays.dto.response.AccessResponse;
import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.dto.response.RoleResponse;
import by.magofrays.entity.Access;
import by.magofrays.service.RoleService;
import by.magofrays.validation.UpdateGroup;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/role")
public class RoleController {
    private final RoleService roleService;


    @GetMapping("{familyId}")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#familyId, 'family', 'SHOW_ROLES')")
    public ResponseEntity<List<RoleResponse>> getFamilyRoles(@PathVariable @NotNull UUID familyId) {
        return ResponseEntity.ok(roleService.getFamilyRoles(familyId));
    }

    @GetMapping("/accesses")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<AccessResponse>> getAccesses() {
        return ResponseEntity.ok(Stream.of(Access.values())
                .map(access ->
                        new AccessResponse(
                                access.name(),
                                access.getDescription()))
                .toList());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'MANAGE_ROLE')")
    public ResponseEntity<RoleResponse> createRole(@RequestBody @Validated CreateUpdateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(request));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'MANAGE_ROLE')")
    public ResponseEntity<?> deleteRole(@RequestBody @Validated DeleteRoleRequest request) {
        roleService.deleteRole(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'MANAGE_ROLE')")
    public ResponseEntity<RoleResponse> updateRole(
            @RequestBody @Validated({UpdateGroup.class}) CreateUpdateRoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(request));
    }

    @PostMapping("/attach")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'MANAGE_MEMBER_ROLES')")
    public ResponseEntity<ReadFamilyMemberDto> attachRole(
            @RequestBody @Validated AttachRoleRequest request
    ) {
        return ResponseEntity.ok(roleService.attachRoleToMember(request));
    }

    @PostMapping("/detach")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'MANAGE_MEMBER_ROLES')")
    public ResponseEntity<ReadFamilyMemberDto> detachRole(
            @RequestBody @Validated AttachRoleRequest request
    ) {
        return ResponseEntity.ok(roleService.detachRoleFromMember(request));
    }
}
