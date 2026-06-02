package by.magofrays.dto.response;

import by.magofrays.entity.Access;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RoleDto {
    private UUID id;
    private String name;
    private Integer value;
    private List<Access> accessList;
    private Integer memberCount;
}