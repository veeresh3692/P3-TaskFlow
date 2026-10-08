package com.taskflow.service;

import com.taskflow.dto.ProjectRequest;
import com.taskflow.dto.ProjectResponse;
import com.taskflow.entity.Project;
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
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository,
                          SprintRepository sprintRepository,
                          TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project(request.getName(), request.getDescription());
        Project saved = projectRepository.save(project);
        log.info("Created project id={} name='{}'", saved.getId(), saved.getName());
        return mapToResponse(saved);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        Project updated = projectRepository.save(project);
        log.info("Updated project id={}", updated.getId());
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
        log.info("Deleted project id={}", id);
    }

    private ProjectResponse mapToResponse(Project project) {
        long taskCount = taskRepository.findByProjectId(project.getId()).size();
        long sprintCount = sprintRepository.findByProjectId(project.getId()).size();
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                taskCount,
                sprintCount,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
