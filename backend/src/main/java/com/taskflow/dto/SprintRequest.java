package com.taskflow.dto;

import com.taskflow.common.enums.SprintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class SprintRequest {

    @NotBlank(message = "Sprint name is required")
    @Size(min = 2, max = 150, message = "Sprint name must be between 2 and 150 characters")
    private String name;

    @Size(max = 2000, message = "Goal cannot exceed 2000 characters")
    private String goal;

    private LocalDate startDate;
    private LocalDate endDate;

    private SprintStatus status = SprintStatus.PLANNED;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    public SprintRequest() {}

    public SprintRequest(String name, String goal, LocalDate startDate, LocalDate endDate, SprintStatus status, Long projectId) {
        this.name = name;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : SprintStatus.PLANNED;
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
