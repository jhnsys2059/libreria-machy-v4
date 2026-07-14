package com.machy.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequest {
    @NotBlank(message = "Nombre es requerido")
    private String nombre;

    @NotBlank(message = "Apellidos son requeridos")
    private String apellidos;

    private String dni;
    private String telefono;

    @NotBlank(message = "Correo es requerido")
    @Email(message = "Correo debe ser valido")
    private String correo;

    @NotBlank(message = "Username es requerido")
    private String username;

    private String password;
    private String rol;
    private String turno;

    public UserRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
}
