package com.ventaslibros.application.dto.request;

import jakarta.validation.constraints.*;

public class ClienteRequest {
    @NotBlank @Size(max = 120)
    private String nombres;
    @NotBlank @Size(max = 120)
    private String apellidos;
    @NotBlank @Size(max = 20)
    private String tipoDocumento;
    @NotBlank @Size(max = 20)
    private String numeroDocumento;
    @Email @Size(max = 150)
    private String email;
    @Size(max = 20)
    private String telefono;
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
