package by.magofrays.controller;

import by.magofrays.dto.request.CreateUpdateTaskRequest;
import by.magofrays.dto.request.DeleteTaskRequest;
import by.magofrays.dto.request.MarkCheckTaskRequest;
import by.magofrays.dto.response.ReadTaskDto;
import by.magofrays.exception.BusinessException;
import by.magofrays.service.TaskService;
import by.magofrays.validation.UpdateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<ReadTaskDto>> getTasksByMember(
            @AuthenticationPrincipal Jwt principal) {
        var tasks = taskService.getTasksByIssuedToId(UUID.fromString(principal.getId()));
        return ResponseEntity
                .ok(tasks);
    }

    @PostMapping("/create")
    @PreAuthorize("""
                hasAuthority('USER') && hasPermission(#createTaskDto.familyId, 'family', 'CREATE_TASK')
                && (#createTaskDto.issuedTo == null || hasPermission(#createTaskDto.familyId, 'family', 'ASSIGN_TASK'))
            """)
    public ResponseEntity<ReadTaskDto> createTask(
            @AuthenticationPrincipal Jwt principal,
            @Validated @RequestBody CreateUpdateTaskRequest createTaskDto
    ) {
        UUID memberId = UUID.fromString(principal.getId());
        var task = taskService.createTask(createTaskDto, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(task);
    }

    @PutMapping("/update")
    @PreAuthorize(
            """
                        hasAuthority('USER') && (hasPermission(#updateTaskDto.familyId, 'family', 'UPDATE_TASK')
                        || taskService.isCreatedBy(#principal.id, #updateTaskDto.taskId))
                        && (#updateTaskDto.issuedTo == null || hasPermission(#updateTaskDto.familyId, 'family', 'ASSIGN_TASK'))
                    """
    )
    public ResponseEntity<ReadTaskDto> updateTask(@AuthenticationPrincipal Jwt principal,
                                                  @Validated({UpdateGroup.class}) @RequestBody CreateUpdateTaskRequest updateTaskDto
    ) {
        UUID memberId = UUID.fromString(principal.getId());
        var task = taskService.updateTask(updateTaskDto, memberId);
        return ResponseEntity.ok(task);

    }


    @PostMapping("/mark-check")
    @PreAuthorize("""
                hasAuthority('USER')
            """
    )

    public void markOrCheckTask(
            @AuthenticationPrincipal Jwt principal,
            @Validated @RequestBody MarkCheckTaskRequest markOrCheckTaskDto) {
        if (markOrCheckTaskDto.taskMarked() == null && markOrCheckTaskDto.taskChecked() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "В запросе должна быть отметка проверки либо отметка выполненности!");
        }
        taskService.markOrCheckTask(markOrCheckTaskDto, UUID.fromString(principal.getId()));
    }

    @GetMapping("/{familyId}")
    public ResponseEntity<List<ReadTaskDto>> getFamilyTasks(@PathVariable UUID familyId,
                                                            @AuthenticationPrincipal Jwt principal) {
        var tasks = taskService.getFamilyTasks(familyId, UUID.fromString(principal.getId()));
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping
    @PreAuthorize("""
                hasAuthority('USER') &&
                (hasPermission(#deleteTaskRequest.familyId, 'family', 'DELETE_TASK') || taskService.isCreatedBy(#principal.id, #deleteTaskRequest.taskId))
            """)
    public ResponseEntity<?> deleteTask(
            @Validated @RequestBody DeleteTaskRequest deleteTaskRequest,
            @AuthenticationPrincipal Jwt principal
    ) {
        UUID memberId = UUID.fromString(principal.getId());
        taskService.deleteTask(deleteTaskRequest, memberId);
        return ResponseEntity.noContent().build();
    }
}
