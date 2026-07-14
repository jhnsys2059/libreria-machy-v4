package com.machy.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "config")
public class Config {

    @Id
    @Column(nullable = false, length = 100)
    private String clave;

    @Column(columnDefinition = "TEXT")
    private String valor;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Config() {}

    public Config(String clave, String valor) {
        this.clave = clave;
        this.valor = valor;
        this.updatedAt = Instant.now();
    }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }
}
