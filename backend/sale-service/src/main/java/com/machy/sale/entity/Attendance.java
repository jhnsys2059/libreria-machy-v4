package com.machy.sale.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "asistencia")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(length = 200)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(length = 20)
    private String turno;

    @Column(precision = 5, scale = 2)
    private BigDecimal horas;

    @Column(name = "tardanza_min")
    private Integer tardanzaMin;

    @Column(name = "cumple_turno")
    private Boolean cumpleTurno;

    @Column(name = "estado_asistencia", length = 20)
    private String estadoAsistencia;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public Attendance() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }
    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public BigDecimal getHoras() { return horas; }
    public void setHoras(BigDecimal horas) { this.horas = horas; }
    public Integer getTardanzaMin() { return tardanzaMin; }
    public void setTardanzaMin(Integer tardanzaMin) { this.tardanzaMin = tardanzaMin; }
    public Boolean getCumpleTurno() { return cumpleTurno; }
    public void setCumpleTurno(Boolean cumpleTurno) { this.cumpleTurno = cumpleTurno; }
    public String getEstadoAsistencia() { return estadoAsistencia; }
    public void setEstadoAsistencia(String estadoAsistencia) { this.estadoAsistencia = estadoAsistencia; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }
}
