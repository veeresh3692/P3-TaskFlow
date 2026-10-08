package com.taskflow.dto;

import com.taskflow.common.enums.SprintStatus;

import java.time.Instant;
import java.time.LocalDate;

public class SprintResponse {
    private Long id;
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private SprintStatus status;
    private Long projectId;
    private String projectName;
    private long taskCount;
    private long completedTaskCount;
    private Instant createdAt;
    private Instant updatedAt;

    public SprintResponse() {}

    public SprintResponse(Long id, String name, String goal, LocalDate startDate, LocalDate endDate,
                          SprintStatus status, Long projectId, String projectName,
                          long taskCount, long completedTaskCount, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.projectId = projectId;
        this.projectName = projectName;
        this.taskCount = taskCount;
        this.completedTaskCount = completedTaskCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }

    public long getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(long completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
