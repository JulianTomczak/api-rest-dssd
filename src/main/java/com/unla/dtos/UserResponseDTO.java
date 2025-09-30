package com.unla.dtos;

public class UserResponseDTO {
    private Integer id;
    private String name;
    private String mail;

    public UserResponseDTO(Integer id, String name, String mail) {
        this.id = id;
        this.name = name;
        this.mail = mail;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }
}
