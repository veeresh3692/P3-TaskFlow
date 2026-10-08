package com.taskflow.taskservice.dto;

import com.taskflow.common.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private TaskStatus status;

    public TaskStatusUpdateRequest() {}
    public TaskStatusUpdateRequest(TaskStatus status) { this.status = status; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}
