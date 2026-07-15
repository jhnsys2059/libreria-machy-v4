package com.machy.sale.service;

import com.machy.sale.client.AuthClient;
import com.machy.sale.entity.Attendance;
import com.machy.sale.entity.LogEntry;
import com.machy.sale.repository.AttendanceRepository;
import com.machy.sale.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class AttendanceService {

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    private final AttendanceRepository attendanceRepository;
    private final LogRepository logRepository;
    private final AuthClient authClient;

    public AttendanceService(AttendanceRepository attendanceRepository, LogRepository logRepository,
                             AuthClient authClient) {
        this.attendanceRepository = attendanceRepository;
        this.logRepository = logRepository;
        this.authClient = authClient;
    }

    public List<Attendance> findAll() {
        return attendanceRepository.findAllByOrderByFechaDesc();
    }

    public Map<String, Object> getStatusHoy(UUID usuarioId) {
        LocalDate hoy = LocalDate.now(ZONE);
        var opt = attendanceRepository.findByUsuarioIdAndFecha(usuarioId, hoy);
        if (opt.isPresent()) {
            Attendance reg = opt.get();
            Map<String, Object> res = new HashMap<>();
            res.put("registrado", true);
            res.put("horaEntrada", reg.getHoraEntrada() != null ? reg.getHoraEntrada().toString() : null);
            res.put("horaSalida", reg.getHoraSalida() != null ? reg.getHoraSalida().toString() : null);
            res.put("turno", reg.getTurno());
            res.put("tardanzaMin", reg.getTardanzaMin());
            res.put("estado", reg.getEstadoAsistencia());
            return res;
        }
        Map<String, Object> empty = new HashMap<>();
        empty.put("registrado", false);
        return empty;
    }

    @Transactional
    public Attendance marcarEntrada(UUID usuarioId) {
        var userResponse = authClient.getUserById(usuarioId.toString());
        if (Boolean.FALSE.equals(userResponse.get("success"))) {
            throw new RuntimeException("Usuario no encontrado");
        }
        var userData = (Map<?, ?>) userResponse.get("data");
        String nombreCompleto = (String) userData.get("nombreCompleto");
        String turno = userData.get("turno") != null ? (String) userData.get("turno") : "completo";

        LocalDate hoy = LocalDate.now(ZONE);
        if (attendanceRepository.findByUsuarioIdAndFecha(usuarioId, hoy).isPresent()) {
            throw new RuntimeException("Ya tienes un registro de entrada hoy");
        }

        LocalTime ahora = LocalTime.now(ZONE);
        int tardanzaMin = calcularTardanza(ahora, turno);

        Attendance attendance = new Attendance();
        attendance.setUsuarioId(usuarioId);
        attendance.setNombre(nombreCompleto);
        attendance.setFecha(hoy);
        attendance.setHoraEntrada(ahora);
        attendance.setTurno(turno);
        attendance.setHoras(BigDecimal.ZERO);
        attendance.setTardanzaMin(tardanzaMin);
        attendance.setCumpleTurno(false);
        attendance.setEstadoAsistencia(tardanzaMin > 0 ? "tardanza" : "puntual");

        logRepository.save(LogEntry.builder()
                .nivel("info").modulo("asistencia")
                .mensaje("Entrada: " + nombreCompleto)
                .usuarioId(usuarioId)
                .build());

        return attendanceRepository.save(attendance);
    }

    @Transactional
    public Attendance marcarSalida(UUID usuarioId) {
        LocalDate hoy = LocalDate.now(ZONE);
        Attendance reg = attendanceRepository.findByUsuarioIdAndFecha(usuarioId, hoy)
                .orElseThrow(() -> new RuntimeException("No hay registro de entrada hoy"));

        if (reg.getHoraSalida() != null) {
            throw new RuntimeException("Ya registraste salida hoy");
        }

        LocalTime ahora = LocalTime.now(ZONE);
        reg.setHoraSalida(ahora);

        long minsEntrada = reg.getHoraEntrada().getHour() * 60L + reg.getHoraEntrada().getMinute();
        long minsSalida = ahora.getHour() * 60L + ahora.getMinute();
        BigDecimal horasTrab = BigDecimal.valueOf((minsSalida - minsEntrada) / 60.0)
                .setScale(2, RoundingMode.HALF_UP);

        reg.setHoras(horasTrab);
        reg.setCumpleTurno(horasTrab.compareTo(new BigDecimal("5")) >= 0);

        logRepository.save(LogEntry.builder()
                .nivel("info").modulo("asistencia")
                .mensaje("Salida: " + reg.getNombre())
                .usuarioId(usuarioId)
                .build());

        return attendanceRepository.save(reg);
    }

    public List<Map<String, Object>> getInformeSemanal() {
        var allAttendance = attendanceRepository.findAll();
        Map<UUID, List<Attendance>> byUser = new HashMap<>();
        for (Attendance a : allAttendance) {
            byUser.computeIfAbsent(a.getUsuarioId(), k -> new ArrayList<>()).add(a);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : byUser.entrySet()) {
            List<Attendance> registros = entry.getValue();
            long dias = registros.size();
            double hrs = registros.stream()
                    .mapToDouble(a -> a.getHoras() != null ? a.getHoras().doubleValue() : 0)
                    .sum();
            long tardanzas = registros.stream()
                    .filter(a -> "tardanza".equals(a.getEstadoAsistencia())).count();
            long cumplen = registros.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getCumpleTurno())).count();
            int pct = dias > 0 ? (int) (cumplen * 100 / dias) : 0;

            Map<String, Object> info = new LinkedHashMap<>();
            info.put("usuarioId", entry.getKey().toString());
            info.put("nombre", registros.get(0).getNombre());
            info.put("turno", registros.get(0).getTurno());
            info.put("dias", dias);
            info.put("horas", String.format("%.1f", hrs));
            info.put("tardanzas", tardanzas);
            info.put("cumplimiento", pct + "%");
            info.put("estado", pct >= 80 ? "Cumple" : pct >= 50 ? "Parcial" : "No cumple");
            result.add(info);
        }
        return result;
    }

    public List<Map<String, Object>> getLogDetalle() {
        var all = attendanceRepository.findAllByOrderByFechaDesc();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Attendance a : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", a.getId());
            row.put("usuarioId", a.getUsuarioId());
            row.put("nombre", a.getNombre());
            row.put("fecha", a.getFecha() != null ? a.getFecha().toString() : null);
            row.put("horaEntrada", a.getHoraEntrada() != null ? a.getHoraEntrada().toString() : null);
            row.put("horaSalida", a.getHoraSalida() != null ? a.getHoraSalida().toString() : null);
            row.put("turno", a.getTurno());
            row.put("horas", a.getHoras() != null ? a.getHoras().toString() : "0");
            row.put("tardanzaMin", a.getTardanzaMin() != null ? a.getTardanzaMin() : 0);
            row.put("cumpleTurno", a.getCumpleTurno());
            row.put("estado", a.getEstadoAsistencia());
            result.add(row);
        }
        return result;
    }

    private int calcularTardanza(LocalTime horaActual, String turno) {
        var config = Map.of(
                "manana", LocalTime.of(8, 0),
                "tarde", LocalTime.of(14, 0),
                "completo", LocalTime.of(8, 0)
        );
        LocalTime inicio = config.getOrDefault(turno, LocalTime.of(8, 0));
        long mins = horaActual.getHour() * 60L + horaActual.getMinute();
        long inicioMins = inicio.getHour() * 60L + inicio.getMinute();
        return Math.max(0, (int) (mins - inicioMins - 15));
    }
}
