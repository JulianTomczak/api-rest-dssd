package com.unla.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unla.configuration.JwtService;
import com.unla.dtos.AuthRequest;
import com.unla.dtos.AuthResponse;
import com.unla.dtos.UserRequestDTO;
import com.unla.entities.User;
import com.unla.exception.EmailYaRegistradoException;
import com.unla.services.IUserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtService.generateToken(request.getUsername());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO requestDTO) {
        try {
            User nuevoUsuario = userService.crear(mapToEntity(requestDTO));
            String token = jwtService.generateToken(nuevoUsuario.getMail());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (EmailYaRegistradoException e) {
            return ResponseEntity.badRequest().body("Email ya registrado");
        }
    }

    private User mapToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setMail(dto.getMail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.USER);
        return user;
    }
}
