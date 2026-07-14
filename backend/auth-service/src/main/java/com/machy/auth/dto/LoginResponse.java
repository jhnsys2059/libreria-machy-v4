package com.machy.auth.dto;

public class LoginResponse {
    private String token;
    private String id;
    private String nombre;
    private String apellidos;
    private String username;
    private String rol;
    private String turno;
    private String av;

    public LoginResponse() {}

    public LoginResponse(String token, String id, String nombre, String apellidos,
                         String username, String rol, String turno, String av) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.username = username;
        this.rol = rol;
        this.turno = turno;
        this.av = av;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public String getAv() { return av; }
    public void setAv(String av) { this.av = av; }
}
