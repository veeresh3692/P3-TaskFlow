package com.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.dto.TaskStatusUpdateRequest;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetTaskById_Success() throws Exception {
        TaskResponse response = new TaskResponse(
                1L, "Design System", "Description", TaskStatus.TODO, TaskPriority.HIGH,
                LocalDate.now(), null, null, 1L, "Platform", null, null,
                Instant.now(), Instant.now()
        );

        when(taskService.getTaskById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Design System"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void testCreateTask_Success() throws Exception {
        TaskRequest request = new TaskRequest("Design System", "Description", TaskStatus.TODO,
                TaskPriority.HIGH, LocalDate.now(), null, 1L, null);

        TaskResponse response = new TaskResponse(
                1L, "Design System", "Description", TaskStatus.TODO, TaskPriority.HIGH,
                LocalDate.now(), null, null, 1L, "Platform", null, null,
                Instant.now(), Instant.now()
        );

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Design System"));
    }

    @Test
    void testUpdateTaskStatus_Success() throws Exception {
        TaskStatusUpdateRequest statusRequest = new TaskStatusUpdateRequest(TaskStatus.DONE);
        TaskResponse response = new TaskResponse(
                1L, "Design System", "Description", TaskStatus.DONE, TaskPriority.HIGH,
                LocalDate.now(), null, null, 1L, "Platform", null, null,
                Instant.now(), Instant.now()
        );

        when(taskService.updateTaskStatus(eq(1L), eq(TaskStatus.DONE))).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }
}
