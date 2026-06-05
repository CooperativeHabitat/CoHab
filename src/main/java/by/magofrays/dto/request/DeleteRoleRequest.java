package by.magofrays.dto.request;


import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteRoleRequest(
        @NotNull
        UUID familyId,
        @NotNull
        UUID roleId
) {
}
