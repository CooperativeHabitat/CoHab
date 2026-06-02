package by.magofrays.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record InvitationRequest(
        @NotBlank(message = "Ссылка должна быть корректной")
        @Size(min = 8, max = 8, message = "Длинна кода должна быть 8 символов")
        String code
) {
}
