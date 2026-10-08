package com.taskflow.dto;

import java.time.Instant;

public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private long taskCount;
    private long sprintCount;
    private Instant createdAt;
    private Instant updatedAt;

    public ProjectResponse() {}

    public ProjectResponse(Long id, String name, String description, long taskCount, long sprintCount, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskCount = taskCount;
        this.sprintCount = sprintCount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }

    public long getSprintCount() {
        return sprintCount;
    }

    public void setSprintCount(long sprintCount) {
        this.sprintCount = sprintCount;
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
