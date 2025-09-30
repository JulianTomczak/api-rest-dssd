package com.unla.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.unla.entities.User;

public interface IUserService {
    public Page<User> obtenerTodos(Pageable pageable);
    public Optional<User> findById(Integer id);
    public User crear(User usuario);
    public void eliminar(Integer id);
    public Optional<User> findByMail(String mail);
}
