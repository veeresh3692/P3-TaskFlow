package com.taskflow.controller;

import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.dto.TaskStatusUpdateRequest;
import com.taskflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task CRUD, Jira-style filtering, search, and status updates")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Search, filter, sort and paginate tasks (Jira-style)")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @Parameter(description = "Filter by project ID") @RequestParam(value = "projectId", required = false) Long projectId,
            @Parameter(description = "Filter by sprint ID") @RequestParam(value = "sprintId", required = false) Long sprintId,
            @Parameter(description = "Filter by assignee user ID") @RequestParam(value = "assigneeId", required = false) Long assigneeId,
            @Parameter(description = "Filter by task status") @RequestParam(value = "status", required = false) TaskStatus status,
            @Parameter(description = "Filter by task priority") @RequestParam(value = "priority", required = false) TaskPriority priority,
            @Parameter(description = "Filter by due date") @RequestParam(value = "dueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @Parameter(description = "Full text search keyword in title or description") @RequestParam(value = "search", required = false) String search,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TaskResponse> tasks = taskService.getFilteredTasks(
                projectId, sprintId, assigneeId, status, priority, dueDate, search, pageable
        );
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status (Kanban drag-and-drop / column transition)")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
