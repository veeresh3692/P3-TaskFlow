package com.taskflow.controller;

import com.taskflow.common.enums.SprintStatus;
import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.dto.DashboardStatsResponse;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.SprintRepository;
import com.taskflow.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "High-level metrics and project health overview")
public class DashboardController {

    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;

    public DashboardController(ProjectRepository projectRepository,
                               SprintRepository sprintRepository,
                               TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated dashboard statistics")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        long totalProjects = projectRepository.count();
        long activeSprints = sprintRepository.countByStatus(SprintStatus.ACTIVE);
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByStatus(TaskStatus.DONE);
        long highPriorityTasks = taskRepository.countByPriority(TaskPriority.HIGH) +
                taskRepository.countByPriority(TaskPriority.CRITICAL);

        Map<String, Long> statusCounts = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            statusCounts.put(status.name(), taskRepository.countByStatus(status));
        }

        Map<String, Long> priorityCounts = new HashMap<>();
        for (TaskPriority priority : TaskPriority.values()) {
            priorityCounts.put(priority.name(), taskRepository.countByPriority(priority));
        }

        DashboardStatsResponse stats = new DashboardStatsResponse(
                totalProjects,
                activeSprints,
                totalTasks,
                completedTasks,
                highPriorityTasks,
                statusCounts,
                priorityCounts
        );

        return ResponseEntity.ok(stats);
    }
}
