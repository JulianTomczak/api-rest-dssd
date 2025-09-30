package com.unla.services;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.unla.entities.Task;

public interface ITaskService {
    public Page<Task> obtenerTodos(Pageable pageable);

    public Optional<Task> findById(Integer id);

    public Page<Task> findByUserId(Integer userId, Pageable pageable);

    public Task crear(Task tarea);

    public void eliminar(Integer id);

    public Page<Task> buscarConFiltros(String title, Boolean completed, LocalDate dueBefore, Integer userId,
            Pageable pageable);
}
