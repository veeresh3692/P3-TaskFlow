package com.taskflow.common.dto;

import com.taskflow.common.enums.NotificationType;

import java.time.Instant;

public class NotificationDto {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private Long relatedEntityId;
    private boolean read;
    private Instant createdAt;

    public NotificationDto() {}

    public NotificationDto(Long id, Long userId, NotificationType type, String message,
                           Long relatedEntityId, boolean read, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
