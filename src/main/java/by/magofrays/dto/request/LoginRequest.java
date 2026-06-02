package by.magofrays.dto.request;


import by.magofrays.entity.Member;
import by.magofrays.validation.LoginGroup;
import by.magofrays.validation.RegistrationGroup;
import by.magofrays.validation.Unique;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        @Unique(
                message = "Пользователь с таким никнеймом уже существует",
                entityClass = Member.class,
                fieldName = "username",
                groups = RegistrationGroup.class)
        @NotBlank(message = "Никнейм не может быть пустым")
        String username,
        @NotBlank(
                message = "Введите пароль",
                groups = {RegistrationGroup.class, LoginGroup.class})
        String password
) {
}
