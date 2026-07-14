package com.machy.sale.repository;

import com.machy.sale.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findAllByOrderByFechaDesc();
    Optional<Attendance> findByUsuarioIdAndFecha(UUID usuarioId, LocalDate fecha);
    List<Attendance> findByUsuarioIdOrderByFechaDesc(UUID usuarioId);
    long countByCumpleTurnoTrue();
    long countByEstadoAsistencia(String estado);
}
