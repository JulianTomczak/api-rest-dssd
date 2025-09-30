package com.unla.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 8, max = 50, message = "El nombre debe tener entre 8 y 50 caracteres")
    private String name;

    @NotBlank(message = "El password es obligatorio")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
        message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número"
    )
    private String password;

    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String mail;

    @NotBlank(message = "El rol es obligatorio")
    private String role;
}
