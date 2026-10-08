package com.taskflow.service;

import com.taskflow.dto.ProjectRequest;
import com.taskflow.dto.ProjectResponse;
import com.taskflow.entity.Project;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.SprintRepository;
import com.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project("Platform Project", "Agile workspace");
        testProject.setId(1L);
    }

    @Test
    void testCreateProject_Success() {
        ProjectRequest request = new ProjectRequest("Platform Project", "Agile workspace");
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        ProjectResponse response = projectService.createProject(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Platform Project", response.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testGetProjectById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(taskRepository.findByProjectId(1L)).thenReturn(Collections.emptyList());
        when(sprintRepository.findByProjectId(1L)).thenReturn(Collections.emptyList());

        ProjectResponse response = projectService.getProjectById(1L);

        assertNotNull(response);
        assertEquals("Platform Project", response.getName());
    }

    @Test
    void testGetProjectById_NotFound_ThrowsException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(99L));
    }

    @Test
    void testGetAllProjects_Success() {
        when(projectRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(testProject));
        when(taskRepository.findByProjectId(1L)).thenReturn(Collections.emptyList());
        when(sprintRepository.findByProjectId(1L)).thenReturn(Collections.emptyList());

        List<ProjectResponse> list = projectService.getAllProjects();

        assertNotNull(list);
        assertEquals(1, list.size());
    }
}
