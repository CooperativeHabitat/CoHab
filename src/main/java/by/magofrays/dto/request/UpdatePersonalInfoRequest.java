package by.magofrays.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdatePersonalInfoRequest(
        @NotNull(message = "Пропущено поле никнейм")
        @NotBlank(message = "Никнейм должен быть корректным")
        String username,
        @NotNull @NotBlank
        String firstname,
        @NotNull @NotBlank
        String lastname,
        @NotNull @NotBlank
        LocalDate birthDate
) {
}
