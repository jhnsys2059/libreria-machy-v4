package com.machy.auth.service;

import com.machy.auth.dto.LoginRequest;
import com.machy.auth.dto.LoginResponse;
import com.machy.auth.dto.PasswordRecoveryRequest;
import com.machy.auth.entity.LogEntry;
import com.machy.auth.entity.User;
import com.machy.auth.repository.LogRepository;
import com.machy.auth.repository.UserRepository;
import com.machy.auth.security.JwtUtil;
import com.machy.auth.security.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LogRepository logRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @InjectMocks
    private AuthService authService;

    private User createActiveUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setNombre("Juan");
        user.setApellidos("Perez");
        user.setUsername("juanperez");
        user.setPasswordHash("encoded-password");
        user.setRol("admin");
        user.setTurno("manana");
        user.setActivo(true);
        user.setIntentosFallidos(0);
        user.setCorreo("juan@example.com");
        return user;
    }

    @Test
    void loginSuccess() {
        User user = createActiveUser();
        LoginRequest request = new LoginRequest();
        request.setUsername("JuanPerez");
        request.setPassword("correct-password");

        when(userRepository.findByUsername("juanperez")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct-password", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRol())).thenReturn("jwt-token");
        when(logRepository.save(any(LogEntry.class))).thenReturn(new LogEntry());
        when(userRepository.save(any(User.class))).thenReturn(user);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(user.getId().toString(), response.getId());
        assertEquals(user.getNombre(), response.getNombre());
        assertEquals(user.getApellidos(), response.getApellidos());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getRol(), response.getRol());
        assertEquals(user.getTurno(), response.getTurno());
        assertEquals("JP", response.getAv());

        verify(userRepository).findByUsername("juanperez");
        verify(passwordEncoder).matches("correct-password", "encoded-password");
        verify(jwtUtil).generateToken(user.getId(), user.getUsername(), user.getRol());
        verify(userRepository, times(2)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(0, savedUser.getIntentosFallidos());
        assertNull(savedUser.getBloqueadoHasta());
    }

    @Test
    void loginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("password");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Usuario o contrasena incorrectos", ex.getMessage());
    }

    @Test
    void loginInvalidPassword() {
        User user = createActiveUser();
        LoginRequest request = new LoginRequest();
        request.setUsername("juanperez");
        request.setPassword("wrong-password");

        when(userRepository.findByUsername("juanperez")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Usuario o contrasena incorrectos", ex.getMessage());

        verify(userRepository).save(userCaptor.capture());
        assertEquals(1, userCaptor.getValue().getIntentosFallidos());
    }

    @Test
    void recoverPasswordUserNotFound() {
        PasswordRecoveryRequest request = new PasswordRecoveryRequest();
        request.setUsernameOrEmail("nonexistent@example.com");

        when(userRepository.findByUsernameOrCorreo("nonexistent@example.com", "nonexistent@example.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.recoverPassword(request));
        assertEquals("No encontramos una cuenta con ese dato.", ex.getMessage());
    }

    @Test
    void recoverPasswordUserNotFoundByUsername() {
        PasswordRecoveryRequest request = new PasswordRecoveryRequest();
        request.setUsernameOrEmail("unknown_user");

        when(userRepository.findByUsernameOrCorreo("unknown_user", "unknown_user"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.recoverPassword(request));
        assertEquals("No encontramos una cuenta con ese dato.", ex.getMessage());
    }
}
