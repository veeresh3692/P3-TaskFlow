package com.taskflow.service;

import com.taskflow.common.enums.TaskStatus;
import com.taskflow.dto.SprintRequest;
import com.taskflow.dto.SprintResponse;
import com.taskflow.entity.Project;
import com.taskflow.entity.Sprint;
import com.taskflow.entity.Task;
import com.taskflow.exception.BadRequestException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.SprintRepository;
import com.taskflow.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintService {

    private static final Logger log = LoggerFactory.getLogger(SprintService.class);

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public SprintService(SprintRepository sprintRepository,
                         ProjectRepository projectRepository,
                         TaskRepository taskRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> getAllSprints() {
        return sprintRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> getSprintsByProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project with id " + projectId + " not found");
        }
        return sprintRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SprintResponse getSprintById(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint with id " + id + " not found"));
        return mapToResponse(sprint);
    }

    @Transactional
    public SprintResponse createSprint(SprintRequest request) {
        validateDates(request);

        if (!projectRepository.existsById(request.getProjectId())) {
            throw new ResourceNotFoundException("Project with id " + request.getProjectId() + " not found");
        }

        Sprint sprint = new Sprint(
                request.getName(),
                request.getGoal(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus(),
                request.getProjectId()
        );

        Sprint saved = sprintRepository.save(sprint);
        log.info("Created sprint id={} name='{}'", saved.getId(), saved.getName());
        return mapToResponse(saved);
    }

    @Transactional
    public SprintResponse updateSprint(Long id, SprintRequest request) {
        validateDates(request);

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint with id " + id + " not found"));

        if (!projectRepository.existsById(request.getProjectId())) {
            throw new ResourceNotFoundException("Project with id " + request.getProjectId() + " not found");
        }

        sprint.setName(request.getName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setStatus(request.getStatus());
        sprint.setProjectId(request.getProjectId());

        Sprint updated = sprintRepository.save(sprint);
        log.info("Updated sprint id={}", updated.getId());
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteSprint(Long id) {
        if (!sprintRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sprint with id " + id + " not found");
        }
        sprintRepository.deleteById(id);
        log.info("Deleted sprint id={}", id);
    }

    private void validateDates(SprintRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new BadRequestException("Sprint end date (" + request.getEndDate() +
                        ") must not be before start date (" + request.getStartDate() + ")");
            }
        }
    }

    private SprintResponse mapToResponse(Sprint sprint) {
        String projectName = projectRepository.findById(sprint.getProjectId())
                .map(Project::getName)
                .orElse("Unknown Project");

        List<Task> tasks = taskRepository.findBySprintId(sprint.getId());
        long completedCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();

        return new SprintResponse(
                sprint.getId(),
                sprint.getName(),
                sprint.getGoal(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprint.getStatus(),
                sprint.getProjectId(),
                projectName,
                tasks.size(),
                completedCount,
                sprint.getCreatedAt(),
                sprint.getUpdatedAt()
        );
    }
}
