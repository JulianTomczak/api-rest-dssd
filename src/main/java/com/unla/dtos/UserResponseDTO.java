package com.unla.dtos;

import com.unla.entities.User.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private Integer id;
    private String name;
    private String mail;
    private Role role;
}
