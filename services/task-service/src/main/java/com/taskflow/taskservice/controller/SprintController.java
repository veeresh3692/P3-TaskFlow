package com.taskflow.taskservice.controller;

import com.taskflow.taskservice.dto.SprintRequest;
import com.taskflow.taskservice.dto.SprintResponse;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.service.SprintService;
import com.taskflow.taskservice.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    private final SprintService sprintService;
    private final TaskService taskService;

    public SprintController(SprintService sprintService, TaskService taskService) {
        this.sprintService = sprintService;
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<SprintResponse>> getAll() {
        return ResponseEntity.ok(sprintService.getAllSprints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(sprintService.getSprintById(id));
    }

    @PostMapping
    public ResponseEntity<SprintResponse> create(@Valid @RequestBody SprintRequest request) {
        return new ResponseEntity<>(sprintService.createSprint(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintResponse> update(@PathVariable("id") Long id, @Valid @RequestBody SprintRequest request) {
        return ResponseEntity.ok(sprintService.updateSprint(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.getTasksBySprintId(id));
    }
}
