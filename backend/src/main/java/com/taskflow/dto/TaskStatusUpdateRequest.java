package com.taskflow.dto;

import com.taskflow.common.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusUpdateRequest {

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    public TaskStatusUpdateRequest() {}

    public TaskStatusUpdateRequest(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
