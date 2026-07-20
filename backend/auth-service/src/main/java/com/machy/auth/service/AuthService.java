package com.machy.auth.service;

import com.machy.auth.dto.LoginRequest;
import com.machy.auth.dto.LoginResponse;
import com.machy.auth.dto.PasswordRecoveryRequest;
import com.machy.auth.entity.LogEntry;
import com.machy.auth.entity.User;
import com.machy.auth.repository.LogRepository;
import com.machy.auth.repository.UserRepository;
import com.machy.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, LogRepository logRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.logRepository = logRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        var userOpt = userRepository.findByUsername(request.getUsername().toLowerCase());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario o contrasena incorrectos");
        }

        var user = userOpt.get();

        if (!user.getActivo()) {
            throw new RuntimeException("Cuenta desactivada. Contacta al administrador.");
        }

        if (user.getBloqueadoHasta() != null && user.getBloqueadoHasta().isAfter(Instant.now())) {
            long mins = (user.getBloqueadoHasta().getEpochSecond() - Instant.now().getEpochSecond()) / 60;
            throw new RuntimeException("Cuenta bloqueada. Intenta de nuevo en " + mins + " min");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.setIntentosFallidos(user.getIntentosFallidos() + 1);
            if (user.getIntentosFallidos() >= 5) {
                user.setBloqueadoHasta(Instant.now().plusSeconds(15 * 60));
            }
            userRepository.save(user);

            logRepository.save(LogEntry.builder()
                    .nivel("warning").modulo("auth")
                    .mensaje("Intento fallido de login: " + request.getUsername())
                    .build());

            throw new RuntimeException("Usuario o contrasena incorrectos");
        }

        user.setIntentosFallidos(0);
        user.setBloqueadoHasta(null);
        user.setUltimoAcceso(Instant.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRol());

        logRepository.save(LogEntry.builder()
                .nivel("info").modulo("auth")
                .mensaje("Login exitoso: " + user.getNombreCompleto())
                .usuario(user)
                .build());

        String avatar = (user.getNombre() != null && !user.getNombre().isEmpty()
                && user.getApellidos() != null && !user.getApellidos().isEmpty())
                ? (user.getNombre().charAt(0) + "" + user.getApellidos().charAt(0)).toUpperCase()
                : "??";
        return new LoginResponse(token, user.getId().toString(), user.getNombre(),
                user.getApellidos(), user.getUsername(), user.getRol(), user.getTurno(), avatar);
    }

    @Transactional
    public Map<String, Object> recoverPassword(PasswordRecoveryRequest request) {
        var opt = userRepository.findByUsernameOrCorreo(
                request.getUsernameOrEmail(), request.getUsernameOrEmail());

        if (opt.isEmpty()) {
            throw new RuntimeException("No encontramos una cuenta con ese dato.");
        }

        var user = opt.get();

        if (user.getCorreo() == null || user.getCorreo().isBlank()) {
            throw new RuntimeException("El usuario no tiene un correo registrado. Contacta al administrador.");
        }

        String newPassword = generateTemporalPassword();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        boolean emailSent = emailService.sendPasswordRecovery(
                user.getCorreo(), user.getNombreCompleto(),
                user.getUsername(), newPassword);

        logRepository.save(LogEntry.builder()
                .nivel("info").modulo("auth")
                .mensaje("Contrasena restablecida para: " + request.getUsernameOrEmail()
                        + (emailSent ? " (correo enviado)" : " (correo NO enviado)"))
                .usuario(user)
                .build());

        return Map.of(
                "mensaje", emailSent
                        ? "Tu contrasena ha sido restablecida. Revisa tu correo " + user.getCorreo()
                        : "Tu contrasena ha sido restablecida",
                "username", user.getUsername(),
                "password", newPassword,
                "nombre", user.getNombre(),
                "apellidos", user.getApellidos(),
                "correo", user.getCorreo(),
                "correoEnviado", emailSent
        );
    }

    private String generateTemporalPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User findById(java.util.UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
