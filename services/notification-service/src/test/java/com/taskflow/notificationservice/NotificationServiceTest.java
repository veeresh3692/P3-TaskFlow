package com.taskflow.notificationservice;

import com.taskflow.common.dto.NotificationDto;
import com.taskflow.common.enums.NotificationType;
import com.taskflow.common.enums.TaskEventType;
import com.taskflow.common.event.TaskEvent;
import com.taskflow.notificationservice.consumer.TaskEventConsumer;
import com.taskflow.notificationservice.entity.Notification;
import com.taskflow.notificationservice.repository.NotificationRepository;
import com.taskflow.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification(5L, NotificationType.TASK_ASSIGNED, "Assigned to task", 100L);
        testNotification.setId(1L);
    }

    @Test
    void testGetAllNotifications_Success() {
        when(notificationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(testNotification));

        List<NotificationDto> result = notificationService.getAllNotifications();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Assigned to task", result.get(0).getMessage());
        assertFalse(result.get(0).isRead());
    }

    @Test
    void testMarkAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        NotificationDto result = notificationService.markAsRead(1L);

        assertNotNull(result);
        assertTrue(testNotification.isRead());
        verify(notificationRepository, times(1)).save(testNotification);
    }

    @Test
    void testTaskEventConsumer_ProcessesEventAndSavesNotification() {
        NotificationService mockNotificationService = mock(NotificationService.class);
        TaskEventConsumer consumer = new TaskEventConsumer(mockNotificationService);

        TaskEvent event = new TaskEvent(
                TaskEventType.TASK_ASSIGNED,
                100L,
                "Setup Kafka Consumer",
                1L,
                5L,
                "TODO",
                "IN_PROGRESS",
                2L,
                "Assigned to task"
        );

        consumer.consumeTaskEvent(event);

        verify(mockNotificationService, times(1)).createNotification(any(Notification.class));
    }
}
