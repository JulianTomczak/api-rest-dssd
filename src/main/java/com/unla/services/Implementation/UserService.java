package com.unla.services.Implementation;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.unla.entities.User;
import com.unla.exception.EmailYaRegistradoException;
import com.unla.repositories.UserRepository;
import com.unla.services.IUserService;

@Service("userService")
public class UserService implements IUserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<User> obtenerTodos(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User crear(User usuario) {
        Optional<User> existente = userRepository.findByMail(usuario.getMail());

        if (existente.isPresent()) {
            throw new EmailYaRegistradoException(usuario.getMail());
        }
        return userRepository.save(usuario);
    }

    @Override
    public void eliminar(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        User user = userRepository.findByMail(mail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + mail));

        return new org.springframework.security.core.userdetails.User(
                user.getMail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }

    @Override
    public Optional<User> findByMail(String mail) {
        return userRepository.findByMail(mail);
    }

}
