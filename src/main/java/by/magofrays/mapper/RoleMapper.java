package by.magofrays.mapper;

import by.magofrays.dto.request.CreateUpdateRoleRequest;
import by.magofrays.dto.response.RoleDto;
import by.magofrays.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "memberCount", expression = "java(role.getFamilyMembers().size())")
    RoleDto toDto(Role role);

    @Mapping(target = "name", source = "roleName")
    @Mapping(target = "accessList", source = "accesses")
    Role toEntity(CreateUpdateRoleRequest request);

}
