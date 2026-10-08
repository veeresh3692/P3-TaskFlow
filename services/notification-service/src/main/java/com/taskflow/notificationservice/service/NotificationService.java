package com.taskflow.notificationservice.service;

import com.taskflow.common.dto.NotificationDto;
import com.taskflow.notificationservice.entity.Notification;
import com.taskflow.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDto markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification with id " + id + " not found"));
        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        log.info("Marked notification id={} as read", id);
        return mapToDto(saved);
    }

    @Transactional
    public NotificationDto createNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        log.info("Persisted notification id={} for userId={}", saved.getId(), saved.getUserId());
        return mapToDto(saved);
    }

    public NotificationDto mapToDto(Notification n) {
        return new NotificationDto(n.getId(), n.getUserId(), n.getType(), n.getMessage(), n.getRelatedEntityId(), n.isRead(), n.getCreatedAt());
    }
}
