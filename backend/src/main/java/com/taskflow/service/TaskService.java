package com.taskflow.service;

import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.entity.Project;
import com.taskflow.entity.Sprint;
import com.taskflow.entity.Task;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.SprintRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.TaskSpecification;
import com.taskflow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       SprintRepository sprintRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getFilteredTasks(
            Long projectId,
            Long sprintId,
            Long assigneeId,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueDate,
            String search,
            Pageable pageable
    ) {
        Specification<Task> spec = TaskSpecification.filterTasks(
                projectId, sprintId, assigneeId, status, priority, dueDate, search
        );

        return taskRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return mapToResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project with id " + projectId + " not found");
        }
        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksBySprintId(Long sprintId) {
        if (!sprintRepository.existsById(sprintId)) {
            throw new ResourceNotFoundException("Sprint with id " + sprintId + " not found");
        }
        return taskRepository.findBySprintId(sprintId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        validateForeignKeys(request.getProjectId(), request.getSprintId(), request.getAssigneeId());

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
        log.info("Created task id={} title='{}' in project={}", saved.getId(), saved.getTitle(), saved.getProjectId());
        return mapToResponse(saved);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        validateForeignKeys(request.getProjectId(), request.getSprintId(), request.getAssigneeId());

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setAssigneeId(request.getAssigneeId());
        task.setProjectId(request.getProjectId());
        task.setSprintId(request.getSprintId());

        Task updated = taskRepository.save(task);
        log.info("Updated task id={}", updated.getId());
        return mapToResponse(updated);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);
        Task updated = taskRepository.save(task);
        log.info("Updated task id={} status from {} to {}", id, oldStatus, newStatus);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with id " + id + " not found");
        }
        taskRepository.deleteById(id);
        log.info("Deleted task id={}", id);
    }

    private void validateForeignKeys(Long projectId, Long sprintId, Long assigneeId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project with id " + projectId + " not found");
        }
        if (sprintId != null && !sprintRepository.existsById(sprintId)) {
            throw new ResourceNotFoundException("Sprint with id " + sprintId + " not found");
        }
        if (assigneeId != null && !userRepository.existsById(assigneeId)) {
            throw new ResourceNotFoundException("User assignee with id " + assigneeId + " not found");
        }
    }

    private TaskResponse mapToResponse(Task task) {
        String assigneeName = null;
        if (task.getAssigneeId() != null) {
            assigneeName = userRepository.findById(task.getAssigneeId())
                    .map(User::getName)
                    .orElse("Unknown");
        }

        String projectName = projectRepository.findById(task.getProjectId())
                .map(Project::getName)
                .orElse("Unknown");

        String sprintName = null;
        if (task.getSprintId() != null) {
            sprintName = sprintRepository.findById(task.getSprintId())
                    .map(Sprint::getName)
                    .orElse(null);
        }

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getAssigneeId(),
                assigneeName,
                task.getProjectId(),
                projectName,
                task.getSprintId(),
                sprintName,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
