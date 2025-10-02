package com.unla.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateDTO {
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String title;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    private Boolean completed;

    @FutureOrPresent(message = "La fecha limite debe ser presente o futura")
    private LocalDate dueDate;

    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasCompleted() {
        return completed != null;
    }

    public boolean hasDueDate() {
        return dueDate != null;
    }
}
