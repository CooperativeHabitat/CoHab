package by.magofrays.mapper;

import by.magofrays.dto.request.CreateUpdateRoleRequest;
import by.magofrays.dto.response.AccessResponse;
import by.magofrays.dto.response.RoleResponse;
import by.magofrays.entity.Access;
import by.magofrays.entity.Role;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "memberCount", expression = "java(role.getFamilyMembers().size())")
    @Mapping(target = "accessList", ignore = true)
    RoleResponse toDto(Role role);

    @Mapping(target = "name", source = "roleName")
    @Mapping(target = "accessList", source = "accesses")
    Role toEntity(CreateUpdateRoleRequest request);

    @AfterMapping
    default void toAccessResponse(@MappingTarget RoleResponse response, Role role){
        response.setAccessList(role.getAccessList().stream().map(Access::valueOf)
                .map(access -> new AccessResponse(access.name(), access.getDescription())).toList());
    }
}
