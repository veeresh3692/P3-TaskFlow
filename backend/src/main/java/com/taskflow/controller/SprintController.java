package com.taskflow.controller;

import com.taskflow.dto.SprintRequest;
import com.taskflow.dto.SprintResponse;
import com.taskflow.dto.TaskResponse;
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
@RequestMapping("/api/sprints")
@Tag(name = "Sprints", description = "Sprint planning and lifecycle management endpoints")
public class SprintController {

    private final SprintService sprintService;
    private final TaskService taskService;

    public SprintController(SprintService sprintService, TaskService taskService) {
        this.sprintService = sprintService;
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "List all sprints")
    public ResponseEntity<List<SprintResponse>> getAllSprints() {
        return ResponseEntity.ok(sprintService.getAllSprints());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sprint by ID")
    public ResponseEntity<SprintResponse> getSprintById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(sprintService.getSprintById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new sprint (Admin/Manager)")
    public ResponseEntity<SprintResponse> createSprint(@Valid @RequestBody SprintRequest request) {
        SprintResponse response = sprintService.createSprint(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update an existing sprint (Admin/Manager)")
    public ResponseEntity<SprintResponse> updateSprint(@PathVariable("id") Long id, @Valid @RequestBody SprintRequest request) {
        return ResponseEntity.ok(sprintService.updateSprint(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a sprint (Admin/Manager)")
    public ResponseEntity<Void> deleteSprint(@PathVariable("id") Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sprintId}/tasks")
    @Operation(summary = "Get all tasks belonging to a sprint")
    public ResponseEntity<List<TaskResponse>> getTasksBySprint(@PathVariable("sprintId") Long sprintId) {
        return ResponseEntity.ok(taskService.getTasksBySprintId(sprintId));
    }
}
