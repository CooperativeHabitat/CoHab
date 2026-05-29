package by.magofrays.service;

import by.magofrays.dto.request.CreateUpdateTaskRequest;
import by.magofrays.dto.request.DeleteTaskRequest;
import by.magofrays.dto.request.MarkCheckTaskRequest;
import by.magofrays.dto.response.ReadTaskDto;
import by.magofrays.entity.Task;
import by.magofrays.exception.BusinessException;
import by.magofrays.mapper.TaskMapper;
import by.magofrays.repository.FamilyMemberRepository;
import by.magofrays.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final TaskMapper taskMapper;
    private final NotificationService notificationService;

    public List<ReadTaskDto> getTasksByIssuedToId(UUID id) {
        return taskRepository.getTasksByIssuedToId(id).stream().map(taskMapper::toDto).toList();
    }

    public ReadTaskDto createTask(CreateUpdateTaskRequest taskDto, UUID memberId) {
        log.debug("Member: {} trying to create task", memberId);
        var createdBy = familyMemberRepository.findByMember_IdAndFamily_Id(memberId, taskDto.familyId())
                .orElseThrow(() -> {
                    log.debug("Member: {} for createdBy does not exist in family: {}", memberId, taskDto.familyId());
                    return new BusinessException(HttpStatus.NOT_FOUND,
                            "Пользователь с id: " + memberId + " не состоит в семье: " + taskDto.familyId());
                });

        var task = taskMapper.toEntity(taskDto);
        if (taskDto.issuedTo() != null) {
            var issuedTo = createdBy
                    .getFamily()
                    .getMembers()
                    .stream()
                    .filter(member ->
                            member.getId().equals(taskDto.issuedTo()))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.debug("Member: {} for issuedTo does not exist in family: {}", taskDto.issuedTo(), taskDto.familyId());
                        return new BusinessException(HttpStatus.NOT_FOUND,
                                "Пользователь с id: " + taskDto.issuedTo() + " не состоит в семье: " + taskDto.familyId());
                    });
            task.setIssuedTo(issuedTo);
        } else {
            task.setIssuedTo(createdBy);
        }
        task.setCreatedBy(createdBy);
        task.setCreatedDate(LocalDateTime.now());
        task.setIsChecked(false);
        task.setIsMarked(false);
        task = taskRepository.save(task);
        log.info("Member: {} created task", createdBy.getId());
        notificationService.sendNotificationFamily("create-task",
                "%s создал задачу",
                getClass().getName(),
                createdBy.getFamily(),
                createdBy.getMember().getId());
        notificationService.sendNotificationTask("assign-task",
                "Задача назначена на вас",
                this.getClass().getName(),
                task, createdBy.getMember().getId());
        return taskMapper.toDto(task);
    }

    @Transactional
    private Task getTask(UUID taskId){
        return taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.debug("Task: {} to update does not exist", taskId);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Задачи с id: " + taskId + " не существует");
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReadTaskDto updateTask(CreateUpdateTaskRequest taskDto, UUID memberId) {
        log.debug("Member: {} trying to update task: {}", memberId, taskDto.taskId());
        var task = getTask(taskDto.taskId());
        if (taskDto.taskName() != null) {
            task.setTaskName(taskDto.taskName());
        }
        if (taskDto.description() != null) {
            task.setDescription(taskDto.description());
        }
        if (taskDto.dueDate() != null) {
            task.setDueDate(taskDto.dueDate());
        }
        if (taskDto.issuedTo() != null) {
            task.setIssuedTo(familyMemberRepository
                    .findById(taskDto.issuedTo())
                    .orElseThrow(() -> {
                        log.debug("Member: {} for issuedTo does not exist", taskDto.issuedTo());
                        return new BusinessException(HttpStatus.NOT_FOUND, "Пользователь с id: " + taskDto.issuedTo() + " не существует!");
                    }));
        }
        log.info("Member: {} updating task: {}", memberId, taskDto.taskId());
        task = taskRepository.save(task);
        var familyMember = familyMemberRepository.findByMember_IdAndFamily_Id(memberId, taskDto.familyId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Пользователь с id: " + memberId + " не состоит в семье " + taskDto.familyId()));
        notificationService.sendNotificationFamily("update-task",
                "%s обновил задачу".formatted(familyMember.getMember().getUsername()),
                this.getClass().getName(),
                familyMember.getFamily(),
                familyMember.getMember().getId()
        );
        if (taskDto.issuedTo() != null) {
            notificationService.sendNotificationTask("assign-task",
                    "%s назначил на %s задачу %s"
                            .formatted(familyMember.getMember().getUsername(),
                                    task.getIssuedTo().getMember().getUsername(),
                                    task.getTaskName()),
                    this.getClass().getName(),
                    task,
                    familyMember.getMember().getId()
            );
        }
        return taskMapper.toDto(task);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void markOrCheckTask(MarkCheckTaskRequest markTaskDto, UUID memberId) {
        var task = getTask(markTaskDto.taskId());
        String result = "";
        boolean isMarked = false;
        if (markTaskDto.taskMarked() != null) {
            isMarked = true;
            if (!task.getIssuedTo().getMember().getId().equals(memberId)) {

                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "На пользователя с id: " + memberId + " задача с id: " + markTaskDto.taskId() + " не назначена!");
            } else {
                if (task.getIsChecked() != null && task.getIsChecked()) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST,
                            "Задача с id: " + markTaskDto.taskId() + " уже проверена!");
                }
                task.setIsMarked(markTaskDto.taskMarked());
                if (task.getIsMarked()) {
                    result = "выполнил задачу:";
                } else {
                    result = "убрал отметку выполнения задачи:";
                }
            }
        } else {
            if (markTaskDto.taskChecked() != null && !task.getCreatedBy().getMember().getId().equals(memberId)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "Пользователь с id: " + memberId + " не создавал задачу с id: " + markTaskDto.taskId() + "!");
            } else {
                if (task.getIsMarked() != null && !task.getIsMarked()) {
                    task.setIsMarked(markTaskDto.taskChecked());
                }
                task.setIsChecked(markTaskDto.taskChecked());
            }

            if (task.getIsChecked()) {
                result = "проверил задачу:";
            } else {
                result = "убрал отметку проверки задачи:";
            }

        }
        task = taskRepository.save(task);
        notificationService.sendNotificationTask("mark-check-task",
                "%s %s %s"
                        .formatted(isMarked ? task.getCreatedBy().getMember().getUsername() : task.getIssuedTo().getMember().getUsername(),
                                result,
                                task.getTaskName()),
                this.getClass().getName(),
                task,
                isMarked ? task.getIssuedTo().getMember().getId() : task.getCreatedBy().getMember().getId());

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ReadTaskDto> getFamilyTasks(UUID familyId, UUID memberId) {
        if (!familyMemberRepository.memberInFamily(memberId, familyId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND,
                    "Пользователь с id: " + memberId + " не состоит в семье, либо семьи с id: " + familyId + "не существует!");
        }
        return taskRepository
                .getTasksByCreatedBy_Family_Id(familyId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    public void deleteTask(DeleteTaskRequest taskDto, UUID memberId) {
        var task = taskRepository.findById(taskDto.taskId()).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                "Задачи с id: " + taskDto.taskId() + " не существует!"));
        var familyMember = familyMemberRepository.findByMember_IdAndFamily_Id(memberId, taskDto.familyId())
                .orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND,
                                "Пользователь с id: " + memberId + " не состоит в семье, либо семьи с id: " + taskDto.familyId() + "не существует!"));
        notificationService.sendNotificationFamily("delete-task",
                "%s удалил задачу %s"
                        .formatted(familyMember.getMember().getUsername(), task.getTaskName()),
                this.getClass().getName(),
                familyMember.getFamily(),
                familyMember.getMember().getId()
        );
        taskRepository.delete(task);
    }
}
