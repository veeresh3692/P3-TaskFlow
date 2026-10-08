package com.taskflow.taskservice.service;

import com.taskflow.common.enums.TaskStatus;
import com.taskflow.taskservice.dto.SprintRequest;
import com.taskflow.taskservice.dto.SprintResponse;
import com.taskflow.taskservice.entity.Project;
import com.taskflow.taskservice.entity.Sprint;
import com.taskflow.taskservice.entity.Task;
import com.taskflow.taskservice.repository.ProjectRepository;
import com.taskflow.taskservice.repository.SprintRepository;
import com.taskflow.taskservice.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintService {

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
        return sprintRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> getSprintsByProjectId(Long projectId) {
        return sprintRepository.findByProjectId(projectId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SprintResponse getSprintById(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sprint with id " + id + " not found"));
        return mapToResponse(sprint);
    }

    @Transactional
    public SprintResponse createSprint(SprintRequest request) {
        if (!projectRepository.existsById(request.getProjectId())) {
            throw new IllegalArgumentException("Project with id " + request.getProjectId() + " not found");
        }
        Sprint sprint = new Sprint(request.getName(), request.getGoal(), request.getStartDate(), request.getEndDate(), request.getStatus(), request.getProjectId());
        return mapToResponse(sprintRepository.save(sprint));
    }

    @Transactional
    public SprintResponse updateSprint(Long id, SprintRequest request) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sprint with id " + id + " not found"));
        sprint.setName(request.getName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setStatus(request.getStatus());
        sprint.setProjectId(request.getProjectId());
        return mapToResponse(sprintRepository.save(sprint));
    }

    @Transactional
    public void deleteSprint(Long id) {
        if (!sprintRepository.existsById(id)) {
            throw new IllegalArgumentException("Sprint with id " + id + " not found");
        }
        sprintRepository.deleteById(id);
    }

    private SprintResponse mapToResponse(Sprint sprint) {
        String projectName = projectRepository.findById(sprint.getProjectId()).map(Project::getName).orElse("Unknown");
        List<Task> tasks = taskRepository.findBySprintId(sprint.getId());
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        return new SprintResponse(sprint.getId(), sprint.getName(), sprint.getGoal(), sprint.getStartDate(), sprint.getEndDate(), sprint.getStatus(), sprint.getProjectId(), projectName, tasks.size(), completed, sprint.getCreatedAt(), sprint.getUpdatedAt());
    }
}
