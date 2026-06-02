package by.magofrays.controller;

import by.magofrays.dto.request.CreateRoleRequest;
import by.magofrays.dto.request.UpdateFamilyRequest;
import by.magofrays.dto.response.ReadFamilyDto;
import by.magofrays.dto.response.RoleDto;
import by.magofrays.entity.Access;
import by.magofrays.service.RoleService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/role")
public class RoleController {
    private final RoleService roleService;


    @GetMapping("{familyId}")
    @PreAuthorize("hasAuthority('USER') && hasPermission(#familyId, 'family', 'SHOW_ROLES')")
    public List<RoleDto> getFamilyRoles(@PathVariable @NotNull UUID familyId){
        return roleService.getFamilyRoles(familyId);
    }

    @GetMapping("/accesses")
    @PreAuthorize("hasAuthority('USER')")
    public List<Access> getAccesses(){
        return List.of(Access.values());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER') && hasPermission(#request.familyId, 'family', 'MANAGE_ROLE')")
    public RoleDto createRole(@RequestBody @Validated CreateRoleRequest request){
        return roleService.createRole(request);
    }


}
