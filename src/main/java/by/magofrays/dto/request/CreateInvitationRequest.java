package by.magofrays.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateInvitationRequest(
    @NotNull
    UUID familyId,
    @Min(value = 1, message = "Число пользователей должно быть в диапазоне от 1 до 15")
    @Max(value = 15, message = "Число пользователей должно быть в диапазоне от 1 до 15")
    Integer numMembers,
    @Future(message = "Дата окончания должна быть корректной")
    @NotNull
    LocalDateTime expiresAt
){}
