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
        if (userRepository.count() == 0) {
            seedUsers();
        } else {
            log.info("Users already exist ({} found), skipping seed", userRepository.count());
        }
    }

    private void seedUsers() {
        createUser("admin", "Jhon", "Taipe", "00000000", "jhonelvs1919@gmail.com", "admin123", "admin", "completo");
        createUser("ana", "Ana", "Flores", "11111111", "ana@machy.com", "vendedor123", "vendedor", "completo");
        createUser("miguel", "Miguel", "Torres", "22222222", "miguel@machy.com", "vendedor123", "vendedor", "tarde");
        log.info("Auth Service - Database seeding completed (3 users created)");
    }

    private void createUser(String username, String nombre, String apellidos,
                             String dni, String correo, String password,
                             String rol, String turno) {
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
