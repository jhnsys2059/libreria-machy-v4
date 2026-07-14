package com.machy.auth.config;

import com.machy.auth.entity.User;
import com.machy.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        log.info("Auth Service - Database seeding completed");
    }

    private void seedUsers() {
        saveOrUpdateUser("admin", "Jhon", "Taipe", "00000000", "admin@machy.com", "admin123", "admin", "completo");
        saveOrUpdateUser("ana", "Ana", "Flores", "11111111", "ana@machy.com", "vendedor123", "vendedor", "completo");
        saveOrUpdateUser("miguel", "Miguel", "Torres", "22222222", "miguel@machy.com", "vendedor123", "vendedor", "tarde");
    }

    private void saveOrUpdateUser(String username, String nombre, String apellidos,
                                   String dni, String correo, String password,
                                   String rol, String turno) {
        var opt = userRepository.findByUsername(username);
        if (opt.isPresent()) {
            var user = opt.get();
            user.setNombre(nombre);
            user.setApellidos(apellidos);
            user.setDni(dni);
            user.setCorreo(correo);
            user.setRol(rol);
            user.setTurno(turno);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setActivo(true);
            userRepository.save(user);
        } else {
            var user = new User();
            user.setUsername(username);
            user.setNombre(nombre);
            user.setApellidos(apellidos);
            user.setDni(dni);
            user.setCorreo(correo);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setRol(rol);
            user.setTurno(turno);
            user.setActivo(true);
            user.setIntentosFallidos(0);
            userRepository.save(user);
        }
    }
}
