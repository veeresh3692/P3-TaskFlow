package com.taskflow.taskservice;

import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.taskservice.dto.TaskRequest;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.entity.Project;
import com.taskflow.taskservice.entity.Task;
import com.taskflow.taskservice.repository.ProjectRepository;
import com.taskflow.taskservice.repository.SprintRepository;
import com.taskflow.taskservice.repository.TaskRepository;
import com.taskflow.taskservice.service.TaskEventProducer;
import com.taskflow.taskservice.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private TaskEventProducer taskEventProducer;

    @InjectMocks
    private TaskService taskService;

    private Project testProject;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testProject = new Project("Test Project", "Desc");
        testProject.setId(1L);

        testTask = new Task("Task 1", "Desc", TaskStatus.TODO, TaskPriority.MEDIUM, LocalDate.now(), 5L, 1L, null);
        testTask.setId(10L);
    }

    @Test
    void testCreateTask_PublishesKafkaEvent() {
        TaskRequest request = new TaskRequest("Task 1", "Desc", TaskStatus.TODO, TaskPriority.MEDIUM, LocalDate.now(), 5L, 1L, null);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        verify(taskEventProducer, atLeastOnce()).publishEvent(any());
    }

    @Test
    void testUpdateTaskStatus_PublishesStatusChangeEvent() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        TaskResponse response = taskService.updateTaskStatus(10L, TaskStatus.DONE);

        assertNotNull(response);
        verify(taskEventProducer, times(1)).publishEvent(any());
    }
}
