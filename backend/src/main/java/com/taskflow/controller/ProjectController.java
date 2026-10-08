package com.taskflow.controller;

import com.taskflow.dto.ProjectRequest;
import com.taskflow.dto.ProjectResponse;
import com.taskflow.dto.SprintResponse;
import com.taskflow.dto.TaskResponse;
import com.taskflow.service.ProjectService;
import com.taskflow.service.SprintService;
import com.taskflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    private final ProjectService projectService;
    private final SprintService sprintService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService,
                             SprintService sprintService,
                             TaskService taskService) {
        this.projectService = projectService;
        this.sprintService = sprintService;
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "List all projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new project (Admin/Manager)")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update an existing project (Admin/Manager)")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable("id") Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a project (Admin only)")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectId}/tasks")
    @Operation(summary = "Get all tasks belonging to a project")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @GetMapping("/{projectId}/sprints")
    @Operation(summary = "Get all sprints belonging to a project")
    public ResponseEntity<List<SprintResponse>> getSprintsByProject(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(sprintService.getSprintsByProjectId(projectId));
    }
}
