package com.taskflow.userservice;

import com.taskflow.common.dto.UserDto;
import com.taskflow.common.enums.Role;
import com.taskflow.userservice.entity.User;
import com.taskflow.userservice.repository.UserRepository;
import com.taskflow.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Jane Doe", "jane@example.com", "hash", Role.MANAGER);
        testUser.setId(10L);
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));

        UserDto dto = userService.getUserById(10L);

        assertNotNull(dto);
        assertEquals("Jane Doe", dto.getName());
        assertEquals("jane@example.com", dto.getEmail());
        assertEquals(Role.MANAGER, dto.getRole());
    }

    @Test
    void testGetAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> list = userService.getAllUsers();

        assertEquals(1, list.size());
        assertEquals("Jane Doe", list.get(0).getName());
    }
}
