package com.machy.auth.service;

import com.machy.auth.entity.LogEntry;
import com.machy.auth.entity.User;
import com.machy.auth.repository.LogRepository;
import com.machy.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class BackupService {

    private final UserRepository userRepository;
    private final LogRepository logRepository;

    public BackupService(UserRepository userRepository, LogRepository logRepository) {
        this.userRepository = userRepository;
        this.logRepository = logRepository;
    }

    public Map<String, Object> exportData() {
        List<Map<String, Object>> usersList = new ArrayList<>();
        for (User u : userRepository.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("nombre", u.getNombre());
            map.put("apellidos", u.getApellidos());
            map.put("dni", u.getDni());
            map.put("telefono", u.getTelefono());
            map.put("correo", u.getCorreo());
            map.put("username", u.getUsername());
            map.put("passwordHash", u.getPasswordHash());
            map.put("rol", u.getRol());
            map.put("turno", u.getTurno());
            map.put("activo", u.getActivo());
            map.put("intentosFallidos", u.getIntentosFallidos());
            map.put("bloqueadoHasta", u.getBloqueadoHasta());
            map.put("ultimoAcceso", u.getUltimoAcceso());
            map.put("createdAt", u.getCreatedAt());
            map.put("updatedAt", u.getUpdatedAt());
            usersList.add(map);
        }

        List<Map<String, Object>> logsList = new ArrayList<>();
        for (LogEntry l : logRepository.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", l.getId());
            map.put("nivel", l.getNivel());
            map.put("modulo", l.getModulo());
            map.put("mensaje", l.getMensaje());
            map.put("usuarioId", l.getUsuario() != null ? l.getUsuario().getId() : null);
            map.put("contexto", l.getContexto());
            map.put("createdAt", l.getCreatedAt());
            logsList.add(map);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("version", "4.0.0");
        result.put("service", "auth-service");
        result.put("exportedAt", Instant.now());
        result.put("users", usersList);
        result.put("logs", logsList);
        return result;
    }

    public Map<String, Object> importData(Map<String, Object> backup) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> usersData = (List<Map<String, Object>>) backup.get("users");
        if (usersData == null || usersData.isEmpty()) {
            throw new RuntimeException("El backup no contiene datos de usuarios");
        }

        logRepository.deleteAllInBatch();
        logRepository.flush();

        int userCount = 0;
        Map<UUID, User> savedUsers = new HashMap<>();
        for (Map<String, Object> ud : usersData) {
            UUID id = toUUID(ud.get("id"));
            Optional<User> existing = userRepository.findById(id);
            User user = existing.orElseGet(User::new);
            if (existing.isEmpty()) {
                user.setId(id);
            }
            user.setNombre((String) ud.get("nombre"));
            user.setApellidos((String) ud.get("apellidos"));
            user.setDni((String) ud.get("dni"));
            user.setTelefono((String) ud.get("telefono"));
            user.setCorreo((String) ud.get("correo"));
            user.setUsername((String) ud.get("username"));
            user.setPasswordHash((String) ud.get("passwordHash"));
            user.setRol((String) ud.get("rol"));
            user.setTurno((String) ud.get("turno"));
            user.setActivo(ud.get("activo") != null ? (Boolean) ud.get("activo") : true);
            user.setIntentosFallidos(ud.get("intentosFallidos") != null ? ((Number) ud.get("intentosFallidos")).intValue() : 0);
            user.setBloqueadoHasta(toInstant(ud.get("bloqueadoHasta")));
            user.setUltimoAcceso(toInstant(ud.get("ultimoAcceso")));
            user.setCreatedAt(toInstant(ud.get("createdAt")));
            user.setUpdatedAt(toInstant(ud.get("updatedAt")));

            User saved = userRepository.save(user);
            savedUsers.put(saved.getId(), saved);
            userCount++;
        }

        userRepository.flush();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> logsData = (List<Map<String, Object>>) backup.get("logs");
        int logCount = 0;
        if (logsData != null) {
            for (Map<String, Object> ld : logsData) {
                UUID usuarioId = toUUID(ld.get("usuarioId"));
                User usuario = savedUsers.get(usuarioId);
                if (usuario == null) {
                    continue;
                }

                LogEntry log = new LogEntry();
                log.setNivel((String) ld.get("nivel"));
                log.setModulo((String) ld.get("modulo"));
                log.setMensaje((String) ld.get("mensaje"));
                log.setUsuario(usuario);
                log.setContexto((String) ld.get("contexto"));
                log.setCreatedAt(toInstant(ld.get("createdAt")));

                logRepository.save(log);
                logCount++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("usersImported", userCount);
        result.put("logsImported", logCount);
        return result;
    }

    private UUID toUUID(Object val) {
        if (val == null) return null;
        if (val instanceof UUID) return (UUID) val;
        return UUID.fromString(val.toString());
    }

    private Instant toInstant(Object val) {
        if (val == null) return null;
        if (val instanceof Instant) return (Instant) val;
        if (val instanceof String) return Instant.parse((String) val);
        return null;
    }
}
