package com.unla.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.unla.entities.User;
import com.unla.repositories.UserRepository;
import com.unla.services.Implementation.UserService;

@SpringBootTest
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_Exists() {
        User user = new User(1, "John Doe", "Pass123456", "john@mail.com", User.Role.USER);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_NotExists() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1);

        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testObtenerTodos() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(
                new User(1, "John Doe", "Pass123456", "john@mail.com", User.Role.USER),
                new User(2, "Jane Doe", "Pass123456", "jane@mail.com", User.Role.ADMIN));
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.obtenerTodos(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testCrear() {
        User user = new User(null, "New User", "Pass123456", "new@mail.com", User.Role.USER);
        User savedUser = new User(1, "New User", "Pass123456", "new@mail.com", User.Role.USER);

        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.crear(user);

        assertNotNull(result.getId());
        assertEquals("New User", result.getName());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testEliminar() {
        Integer id = 1;
        doNothing().when(userRepository).deleteById(id);

        userService.eliminar(id);

        verify(userRepository, times(1)).deleteById(id);
    }
}
