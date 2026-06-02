package by.magofrays.dto.request;

import by.magofrays.entity.Access;
import by.magofrays.validation.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateUpdateRoleRequest(
        @NotNull(groups = UpdateGroup.class)
        UUID roleId,
        @NotNull
        UUID familyId,
        @NotBlank(message = "Название роли должно быть корректным")
        String roleName,
        @NotNull
        @Positive(message = "Значение роли должно быть положительным")
        Integer value,
        @NotNull
        List<Access> accesses
) {

}
