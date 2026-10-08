package com.taskflow.taskservice.service;

import com.taskflow.taskservice.dto.ProjectRequest;
import com.taskflow.taskservice.dto.ProjectResponse;
import com.taskflow.taskservice.entity.Project;
import com.taskflow.taskservice.repository.ProjectRepository;
import com.taskflow.taskservice.repository.SprintRepository;
import com.taskflow.taskservice.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

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
                .orElseThrow(() -> new IllegalArgumentException("Project with id " + id + " not found"));
        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project(request.getName(), request.getDescription());
        return mapToResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project with id " + id + " not found"));
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return mapToResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
    }

    private ProjectResponse mapToResponse(Project project) {
        long taskCount = taskRepository.findByProjectId(project.getId()).size();
        long sprintCount = sprintRepository.findByProjectId(project.getId()).size();
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), taskCount, sprintCount, project.getCreatedAt(), project.getUpdatedAt());
    }
}
