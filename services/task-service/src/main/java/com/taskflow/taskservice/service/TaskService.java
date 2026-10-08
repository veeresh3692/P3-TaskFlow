package com.taskflow.taskservice.service;

import com.taskflow.common.enums.TaskEventType;
import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.common.event.TaskEvent;
import com.taskflow.taskservice.dto.TaskRequest;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.entity.Project;
import com.taskflow.taskservice.entity.Sprint;
import com.taskflow.taskservice.entity.Task;
import com.taskflow.taskservice.repository.ProjectRepository;
import com.taskflow.taskservice.repository.SprintRepository;
import com.taskflow.taskservice.repository.TaskRepository;
import com.taskflow.taskservice.repository.TaskSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final TaskEventProducer taskEventProducer;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       SprintRepository sprintRepository,
                       TaskEventProducer taskEventProducer) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.taskEventProducer = taskEventProducer;
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getFilteredTasks(
            Long projectId, Long sprintId, Long assigneeId,
            TaskStatus status, TaskPriority priority, LocalDate dueDate,
            String search, Pageable pageable) {
        Specification<Task> spec = TaskSpecification.filterTasks(projectId, sprintId, assigneeId, status, priority, dueDate, search);
        return taskRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksBySprintId(Long sprintId) {
        return taskRepository.findBySprintId(sprintId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id " + id + " not found"));
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        if (!projectRepository.existsById(request.getProjectId())) {
            throw new IllegalArgumentException("Project with id " + request.getProjectId() + " not found");
        }

        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getDueDate(),
                request.getAssigneeId(),
                request.getProjectId(),
                request.getSprintId()
        );
        Task saved = taskRepository.save(task);

        // Publish Kafka Event
        taskEventProducer.publishEvent(new TaskEvent(
                TaskEventType.TASK_CREATED,
                saved.getId(),
                saved.getTitle(),
                saved.getProjectId(),
                saved.getAssigneeId(),
                null,
                saved.getStatus().name(),
                null,
                "New task created: " + saved.getTitle()
        ));

        if (saved.getAssigneeId() != null) {
            taskEventProducer.publishEvent(new TaskEvent(
                    TaskEventType.TASK_ASSIGNED,
                    saved.getId(),
                    saved.getTitle(),
                    saved.getProjectId(),
                    saved.getAssigneeId(),
                    null,
                    saved.getStatus().name(),
                    null,
                    "Assigned to task: " + saved.getTitle()
            ));
        }

        return mapToResponse(saved);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id " + id + " not found"));

        Long oldAssignee = task.getAssigneeId();
        TaskStatus oldStatus = task.getStatus();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setAssigneeId(request.getAssigneeId());
        task.setProjectId(request.getProjectId());
        task.setSprintId(request.getSprintId());

        Task updated = taskRepository.save(task);

        if (request.getAssigneeId() != null && !request.getAssigneeId().equals(oldAssignee)) {
            taskEventProducer.publishEvent(new TaskEvent(
                    TaskEventType.TASK_ASSIGNED,
                    updated.getId(),
                    updated.getTitle(),
                    updated.getProjectId(),
                    updated.getAssigneeId(),
                    null,
                    updated.getStatus().name(),
                    null,
                    "Assigned to task: " + updated.getTitle()
            ));
        }

        if (request.getStatus() != null && !request.getStatus().equals(oldStatus)) {
            TaskEventType eventType = request.getStatus() == TaskStatus.DONE
                    ? TaskEventType.TASK_COMPLETED
                    : TaskEventType.TASK_STATUS_CHANGED;

            taskEventProducer.publishEvent(new TaskEvent(
                    eventType,
                    updated.getId(),
                    updated.getTitle(),
                    updated.getProjectId(),
                    updated.getAssigneeId(),
                    oldStatus.name(),
                    updated.getStatus().name(),
                    null,
                    "Task status changed from " + oldStatus + " to " + updated.getStatus()
            ));
        }

        return mapToResponse(updated);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id " + id + " not found"));

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);
        Task updated = taskRepository.save(task);

        TaskEventType eventType = newStatus == TaskStatus.DONE
                ? TaskEventType.TASK_COMPLETED
                : TaskEventType.TASK_STATUS_CHANGED;

        taskEventProducer.publishEvent(new TaskEvent(
                eventType,
                updated.getId(),
                updated.getTitle(),
                updated.getProjectId(),
                updated.getAssigneeId(),
                oldStatus.name(),
                newStatus.name(),
                null,
                "Task status changed from " + oldStatus + " to " + newStatus
        ));

        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("Task with id " + id + " not found");
        }
        taskRepository.deleteById(id);
    }

    private TaskResponse mapToResponse(Task task) {
        String projectName = projectRepository.findById(task.getProjectId()).map(Project::getName).orElse("Unknown");
        String sprintName = null;
        if (task.getSprintId() != null) {
            sprintName = sprintRepository.findById(task.getSprintId()).map(Sprint::getName).orElse(null);
        }
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDueDate(), task.getAssigneeId(), "User #" + task.getAssigneeId(), task.getProjectId(), projectName, task.getSprintId(), sprintName, task.getCreatedAt(), task.getUpdatedAt());
    }
}
