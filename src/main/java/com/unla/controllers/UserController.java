package com.unla.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unla.dtos.UserRequestDTO;
import com.unla.dtos.UserResponseDTO;
import com.unla.entities.User;
import com.unla.services.IUserService;

import jakarta.validation.Valid;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/usuarios")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> listarTodos(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<User> usuarios = userService.obtenerTodos(pageable);
        Page<UserResponseDTO> dtoPage = usuarios.map(this::mapToResponseDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> obtenerUsuario(@PathVariable Integer id) {
        return userService.findById(id)
                .map(this::mapToResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> crear(@Valid @RequestBody UserRequestDTO userDTO) {
        User user = mapToEntity(userDTO);
        User creado = userService.crear(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(creado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        if (userService.findById(id).isPresent()) {
            userService.eliminar(id);
            return ResponseEntity.ok("Usuario con ID=" + id + " eliminado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    private User mapToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setMail(dto.getMail());
        user.setRole(User.Role.valueOf(dto.getRole()));
        return user;
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getMail());
    }
}
