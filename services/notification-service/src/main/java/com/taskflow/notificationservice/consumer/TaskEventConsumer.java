package com.taskflow.notificationservice.consumer;

import com.taskflow.common.enums.NotificationType;
import com.taskflow.common.event.TaskEvent;
import com.taskflow.notificationservice.entity.Notification;
import com.taskflow.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TaskEventConsumer.class);

    private final NotificationService notificationService;

    public TaskEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${taskflow.kafka.topics.task-events:task-events}",
            groupId = "notification-group",
            autoStartup = "${taskflow.kafka.enabled:true}"
    )
    public void consumeTaskEvent(TaskEvent event) {
        log.info("Received Kafka TaskEvent: {}", event);

        if (event == null || event.getAssigneeId() == null) {
            log.info("TaskEvent has no assignee; skipping notification creation.");
            return;
        }

        NotificationType notifType;
        String message;

        switch (event.getEventType()) {
            case TASK_ASSIGNED:
                notifType = NotificationType.TASK_ASSIGNED;
                message = "You have been assigned to task: \"" + event.getTitle() + "\"";
                break;
            case TASK_COMPLETED:
                notifType = NotificationType.TASK_COMPLETED;
                message = "Task \"" + event.getTitle() + "\" has been marked as DONE";
                break;
            case TASK_STATUS_CHANGED:
                notifType = NotificationType.TASK_STATUS_CHANGED;
                message = "Task \"" + event.getTitle() + "\" status changed to " + event.getNewStatus();
                break;
            default:
                notifType = NotificationType.TASK_ASSIGNED;
                message = "Task \"" + event.getTitle() + "\" updated";
                break;
        }

        Notification notification = new Notification(
                event.getAssigneeId(),
                notifType,
                message,
                event.getTaskId()
        );

        notificationService.createNotification(notification);
        log.info("Created notification from Kafka event for user {}: {}", event.getAssigneeId(), message);
    }
}
