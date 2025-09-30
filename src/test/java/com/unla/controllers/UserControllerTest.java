package com.unla.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.unla.configuration.JwtAuthenticationFilter;
import com.unla.configuration.JwtService;
import com.unla.entities.User;
import com.unla.services.IUserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testObtenerUsuario_Exists_ReturnsOk() throws Exception {
        // Arrange
        Integer userId = 1;
        User user = new User(userId, "John Doe", "Pass12345","john@example.com", User.Role.ADMIN);

        when(userService.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/usuarios/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.mail").value("john@example.com"));

        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testObtenerUsuario_NotFound_Returns404() throws Exception {
        // Arrange
        Integer userId = 1;
        when(userService.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/usuarios/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testListarTodos_ReturnsOkWithPagedUsers() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        User user1 = new User(1, "John Doe","Pass12345", "john@example.com", User.Role.USER);
        User user2 = new User(2, "Jane Doe","Pass12345", "jane@example.com", User.Role.ADMIN);
        List<User> userList = Arrays.asList(user1, user2);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userService.obtenerTodos(pageable)).thenReturn(userPage);

        // Act & Assert
        mockMvc.perform(get("/usuarios")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.content[0].mail").value("john@example.com"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Jane Doe"))
                .andExpect(jsonPath("$.content[1].mail").value("jane@example.com"))
                .andExpect(jsonPath("$.number").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(userList.size()));

        verify(userService, times(1)).obtenerTodos(pageable);
    }

    @Test
    void testListarTodos_EmptyPage_ReturnsOkWithEmptyContent() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<User> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(userService.obtenerTodos(pageable)).thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/usuarios")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.number").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(userService, times(1)).obtenerTodos(pageable);
    }
}
