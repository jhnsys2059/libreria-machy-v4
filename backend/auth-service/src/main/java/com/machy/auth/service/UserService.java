package com.machy.auth.service;

import com.machy.auth.dto.UserRequest;
import com.machy.auth.entity.User;
import com.machy.auth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByNombre();
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    public User create(UserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Nombre de usuario ya existe");
        }
        if (userRepository.existsByCorreo(req.getCorreo())) {
            throw new RuntimeException("Correo ya registrado");
        }

        User user = new User();
        user.setNombre(req.getNombre());
        user.setApellidos(req.getApellidos());
        user.setDni(req.getDni() != null ? req.getDni() : "");
        user.setTelefono(req.getTelefono() != null ? req.getTelefono() : "");
        user.setCorreo(req.getCorreo());
        user.setUsername(req.getUsername().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRol(req.getRol() != null ? req.getRol() : "vendedor");
        user.setTurno(req.getTurno() != null ? req.getTurno() : "completo");
        user.setActivo(true);
        user.setIntentosFallidos(0);

        return userRepository.save(user);
    }

    public User update(UUID id, UserRequest req) {
        User user = findById(id);

        if (req.getNombre() != null) user.setNombre(req.getNombre());
        if (req.getApellidos() != null) user.setApellidos(req.getApellidos());
        if (req.getDni() != null) user.setDni(req.getDni());
        if (req.getTelefono() != null) user.setTelefono(req.getTelefono());
        if (req.getCorreo() != null) user.setCorreo(req.getCorreo());
        if (req.getRol() != null) user.setRol(req.getRol());
        if (req.getTurno() != null) user.setTurno(req.getTurno());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }

        return userRepository.save(user);
    }

    public User toggleStatus(UUID id) {
        User user = findById(id);
        user.setActivo(!user.getActivo());
        return userRepository.save(user);
    }

    public List<User> findActiveVendors() {
        return userRepository.findByRolAndActivoTrue("vendedor");
    }
}
