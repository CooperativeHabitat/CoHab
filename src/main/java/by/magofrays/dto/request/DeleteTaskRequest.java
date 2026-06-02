package by.magofrays.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteTaskRequest(
        @NotNull UUID taskId,
        @NotNull UUID familyId
) {
}
