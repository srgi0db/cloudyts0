package com.ventaslibros.application.dto.request;

import jakarta.validation.constraints.*;

public class UsuarioRequest {
    @NotBlank @Size(max = 120)
    private String nombres;
    @NotBlank @Size(max = 120)
    private String apellidos;
    @NotBlank @Email @Size(max = 150)
    private String email;
    @NotBlank @Size(min = 6)
    private String password;

    // Getters y Setters
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}