package by.magofrays.dto.request;

import by.magofrays.validation.UpdateGroup;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.sql.Update;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateUpdateTaskRequest(
    @NotNull(groups = {UpdateGroup.class})
    UUID taskId,
    @NotBlank(message = "Название задачи должно быть корректным")
    String taskName,
    String description,
    @NotNull
    UUID familyId,
    UUID issuedTo,
    @Future(message = "Дедлайн должен быть корректным")
    LocalDateTime dueDate
) {

}
