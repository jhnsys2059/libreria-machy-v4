package com.machy.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "logs")
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20)
    private String nivel;

    @Column(length = 100)
    private String modulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @Column(columnDefinition = "TEXT")
    private String contexto;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public LogEntry() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public static LogEntryBuilder builder() { return new LogEntryBuilder(); }

    public static class LogEntryBuilder {
        private String nivel;
        private String modulo;
        private String mensaje;
        private User usuario;
        private String contexto;

        public LogEntryBuilder nivel(String nivel) { this.nivel = nivel; return this; }
        public LogEntryBuilder modulo(String modulo) { this.modulo = modulo; return this; }
        public LogEntryBuilder mensaje(String mensaje) { this.mensaje = mensaje; return this; }
        public LogEntryBuilder usuario(User usuario) { this.usuario = usuario; return this; }
        public LogEntryBuilder contexto(String contexto) { this.contexto = contexto; return this; }

        public LogEntry build() {
            LogEntry entry = new LogEntry();
            entry.setNivel(this.nivel);
            entry.setModulo(this.modulo);
            entry.setMensaje(this.mensaje);
            entry.setUsuario(this.usuario);
            entry.setContexto(this.contexto);
            return entry;
        }
    }
}
