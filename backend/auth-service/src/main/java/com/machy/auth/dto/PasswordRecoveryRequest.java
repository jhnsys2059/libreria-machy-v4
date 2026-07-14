package com.machy.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class PasswordRecoveryRequest {
    @NotBlank(message = "Username o email es requerido")
    private String usernameOrEmail;

    public PasswordRecoveryRequest() {}

    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
}
