package com.unla.services.Implementation;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.unla.dtos.TaskUpdateDTO;
import com.unla.entities.Task;
import com.unla.repositories.TaskRepository;
import com.unla.services.ITaskService;
import com.unla.specification.TaskSpecification;

@Service("taskService")
public class TaskService implements ITaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Page<Task> obtenerTodos(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public Optional<Task> findById(Integer id) {
        return taskRepository.findById(id);
    }

    @Override
    public Page<Task> findByUserId(Integer userId, Pageable pageable) {
        return taskRepository.findByUserId(userId, pageable);
    }

    @Override
    public Task crear(Task tarea) {
        return taskRepository.save(tarea);
    }

    @Override
    public void eliminar(Integer id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Page<Task> buscarConFiltros(String title, Boolean completed, LocalDate dueBefore, Integer userId,
            Pageable pageable) {
        Specification<Task> spec = (root, query, cb) -> cb.conjunction();

        if (userId != null) {
            spec = spec.and(TaskSpecification.belongsToUser(userId));
        }
        if (title != null) {
            spec = spec.and(TaskSpecification.hasTitle(title));
        }

        if (completed != null) {
            spec = spec.and(TaskSpecification.isCompleted(completed));
        }

        if (dueBefore != null) {
            spec = spec.and(TaskSpecification.dueBefore(dueBefore));
        }

        return taskRepository.findAll(spec, pageable);
    }

    @Override
    public Task actualizar(Task task, TaskUpdateDTO actualizacion) {
        task.setTitle(actualizacion.getTitle());
        task.setDescription(actualizacion.getDescription());
        task.setCompleted(actualizacion.getCompleted());
        if (!actualizacion.getDueDate().equals(task.getDueDate())) {
            validateDueDate(actualizacion.getDueDate());
        }
        task.setDueDate(actualizacion.getDueDate());
        return taskRepository.save(task);
    }

    @Override
    public Task patch(Task task, TaskUpdateDTO actualizacion) {
        if (actualizacion.hasTitle()) {
            task.setTitle(actualizacion.getTitle());
        }

        if (actualizacion.hasDescription()) {
            task.setDescription(actualizacion.getDescription());
        }

        if (actualizacion.hasCompleted()) {
            task.setCompleted(actualizacion.getCompleted());
        }

        if (actualizacion.hasDueDate()) {
            if (!actualizacion.getDueDate().equals(task.getDueDate())) {
                validateDueDate(actualizacion.getDueDate());
            }
            task.setDueDate(actualizacion.getDueDate());
        }

        return taskRepository.save(task);
    }

    private void validateDueDate(LocalDate dueDate) {
        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha límite debe ser presente o futura");
        }
    }
}
