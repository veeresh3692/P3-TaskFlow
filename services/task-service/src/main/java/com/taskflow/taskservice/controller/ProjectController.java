package com.taskflow.taskservice.controller;

import com.taskflow.taskservice.dto.ProjectRequest;
import com.taskflow.taskservice.dto.ProjectResponse;
import com.taskflow.taskservice.dto.SprintResponse;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.service.ProjectService;
import com.taskflow.taskservice.service.SprintService;
import com.taskflow.taskservice.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final SprintService sprintService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, SprintService sprintService, TaskService taskService) {
        this.projectService = projectService;
        this.sprintService = sprintService;
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return new ResponseEntity<>(projectService.createProject(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable("id") Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(id));
    }

    @GetMapping("/{id}/sprints")
    public ResponseEntity<List<SprintResponse>> getSprints(@PathVariable("id") Long id) {
        return ResponseEntity.ok(sprintService.getSprintsByProjectId(id));
    }
}
