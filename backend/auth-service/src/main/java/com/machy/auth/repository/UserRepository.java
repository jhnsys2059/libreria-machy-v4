package com.machy.auth.repository;

import com.machy.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrCorreo(String username, String correo);
    boolean existsByUsername(String username);
    boolean existsByCorreo(String correo);
    List<User> findAllByOrderByNombre();
    List<User> findByRolAndActivoTrue(String rol);
}
