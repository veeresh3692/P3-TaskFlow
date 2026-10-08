package com.taskflow.service;

import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.entity.Project;
import com.taskflow.entity.Task;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.SprintRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
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
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private Project testProject;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testProject = new Project("Test Project", "Description");
        testProject.setId(1L);

        testTask = new Task("Test Task", "Desc", TaskStatus.TODO, TaskPriority.HIGH,
                LocalDate.now().plusDays(3), null, 1L, null);
        testTask.setId(10L);
    }

    @Test
    void testCreateTask_Success() {
        TaskRequest request = new TaskRequest("Test Task", "Desc", TaskStatus.TODO, TaskPriority.HIGH,
                LocalDate.now().plusDays(3), null, 1L, null);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Test Task", response.getTitle());
        assertEquals(TaskStatus.TODO, response.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTask_ProjectNotFound_ThrowsException() {
        TaskRequest request = new TaskRequest("Test Task", "Desc", TaskStatus.TODO, TaskPriority.HIGH,
                LocalDate.now().plusDays(3), null, 99L, null);

        when(projectRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTaskStatus_Success() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        TaskResponse response = taskService.updateTaskStatus(10L, TaskStatus.IN_PROGRESS);

        assertNotNull(response);
        assertEquals(TaskStatus.IN_PROGRESS, testTask.getStatus());
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.existsById(10L)).thenReturn(true);

        taskService.deleteTask(10L);

        verify(taskRepository, times(1)).deleteById(10L);
    }

    @Test
    void testDeleteTask_NotFound_ThrowsException() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, never()).deleteById(anyLong());
    }
}
