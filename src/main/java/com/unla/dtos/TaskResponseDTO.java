package com.unla.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private UserResponseDTO user;
}
