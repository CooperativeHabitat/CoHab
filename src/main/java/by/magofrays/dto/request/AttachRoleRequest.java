package by.magofrays.dto.request;


import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AttachRoleRequest(
        @NotNull
        UUID familyId,
        @NotNull
        UUID familyMemberId,
        @NotNull
        String roleName
) {
}
