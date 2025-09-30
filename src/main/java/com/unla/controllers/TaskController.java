package com.unla.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.unla.dtos.TaskRequestDTO;
import com.unla.dtos.TaskResponseDTO;
import com.unla.dtos.UserResponseDTO;
import com.unla.entities.Task;
import com.unla.entities.User;
import com.unla.services.ITaskService;
import com.unla.services.IUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tareas")
public class TaskController {
    @Autowired
    private ITaskService taskService;

    @Autowired
    private IUserService userService;

    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> listarTareas(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        User user = getUserActual(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<Task> tareas;
        if (user.getRole() == User.Role.ADMIN) {
            tareas = taskService.obtenerTodos(pageable);
        } else {
            tareas = taskService.findByUserId(user.getId(), pageable);
        }
        Page<TaskResponseDTO> dtoPage = tareas.map(this::mapToResponseDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> obtenerPorId(@PathVariable Integer id, Authentication authentication) {
        User user = getUserActual(authentication);
        Optional<Task> tareaOptional = taskService.findById(id);
        if (tareaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Task task = tareaOptional.get();
        if (task.getUser().getId().equals(user.getId()) || user.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(mapToResponseDTO(task));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/usuario/{userIdOrMe}")
    public ResponseEntity<TaskResponseDTO> crearTareaParaUsuario(
            @PathVariable String userIdOrMe,
            @Valid @RequestBody TaskRequestDTO dto, Authentication authentication) {

        User actual = getUserActual(authentication);
        Integer userId;

        if ("me".equals(userIdOrMe)) {
            userId = actual.getId(); // usa el ID del usuario logeado
        } else {
            try {
                userId = Integer.parseInt(userIdOrMe);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Validación de permisos
        if (!(userId.equals(actual.getId()) || actual.getRole() == User.Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Task task = mapToEntity(dto);
        task.setUser(user);
        Task creada = taskService.crear(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(creada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id, Authentication authentication) {
        User user = getUserActual(authentication);
        Optional<Task> tareaOptional = taskService.findById(id);
        if (tareaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
        }
        Task task = tareaOptional.get();
        if (task.getUser().getId().equals(user.getId()) || user.getRole() == User.Role.ADMIN) {
            taskService.eliminar(id);
            return ResponseEntity.ok("Tarea con ID=" + id + " eliminada");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<Page<TaskResponseDTO>> listarTareasPorUsuario(@PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Authentication authentication) {

        User user = getUserActual(authentication);
        if (user.getRole() == User.Role.ADMIN || user.getId().equals(userId)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").descending());
            Page<Task> tareas = taskService.findByUserId(userId, pageable);
            Page<TaskResponseDTO> dtoPage = tareas.map(this::mapToResponseDTO);
            return ResponseEntity.ok(dtoPage);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<TaskResponseDTO>> buscarFiltrado(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User actual = getUserActual(authentication);
        if (actual.getRole() != User.Role.ADMIN) {
            userId = actual.getId();
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<Task> tareas = taskService.buscarConFiltros(title, completed, dueBefore, userId, pageable);
        Page<TaskResponseDTO> dtoPage = tareas.map(this::mapToResponseDTO);

        return ResponseEntity.ok(dtoPage);
    }

    private Task mapToEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }

    private TaskResponseDTO mapToResponseDTO(Task task) {
        User user = task.getUser();
        UserResponseDTO userDTO = new UserResponseDTO(user.getId(), user.getName(), user.getMail());

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getDueDate(),
                task.getCreatedAt(),
                userDTO);
    }

    private User getUserActual(Authentication authentication) {
        String mail = authentication.getName();
        return userService.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
