package by.magofrays.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateFamilyRequest(
        @NotNull
        UUID familyId,
        @NotBlank(message = "Название семьи должно быть корректным")
        String familyName
) {

}
