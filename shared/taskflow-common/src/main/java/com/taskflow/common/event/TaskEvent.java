package com.taskflow.common.event;

import com.taskflow.common.enums.TaskEventType;

import java.time.Instant;

public class TaskEvent {
    private TaskEventType eventType;
    private Long taskId;
    private String title;
    private Long projectId;
    private Long assigneeId;
    private String oldStatus;
    private String newStatus;
    private Long triggeredByUserId;
    private String message;
    private Instant timestamp;

    public TaskEvent() {
        this.timestamp = Instant.now();
    }

    public TaskEvent(TaskEventType eventType, Long taskId, String title, Long projectId,
                     Long assigneeId, String oldStatus, String newStatus,
                     Long triggeredByUserId, String message) {
        this.eventType = eventType;
        this.taskId = taskId;
        this.title = title;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.triggeredByUserId = triggeredByUserId;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public TaskEventType getEventType() {
        return eventType;
    }

    public void setEventType(TaskEventType eventType) {
        this.eventType = eventType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Long getTriggeredByUserId() {
        return triggeredByUserId;
    }

    public void setTriggeredByUserId(Long triggeredByUserId) {
        this.triggeredByUserId = triggeredByUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TaskEvent{" +
                "eventType=" + eventType +
                ", taskId=" + taskId +
                ", title='" + title + '\'' +
                ", projectId=" + projectId +
                ", assigneeId=" + assigneeId +
                ", oldStatus='" + oldStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", triggeredByUserId=" + triggeredByUserId +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
