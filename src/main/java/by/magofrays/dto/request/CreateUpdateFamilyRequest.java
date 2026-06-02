package by.magofrays.dto.request;

import by.magofrays.validation.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateUpdateFamilyRequest(
        @NotNull(groups = UpdateGroup.class)
        UUID familyId,
        @NotBlank(message = "Название семьи должно быть корректным")
        @NotNull
        String familyName
) {

}
