package com.ventaslibros.application.dto.response;

public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private String rol;
    
    private String nombres;
    private String apellidos;
    
    private Integer clienteId;
    
    public LoginResponse() {}
    public LoginResponse(String token) { this.token = token; }
    
    public LoginResponse(String token, String tipo, String rol) {
        this.token = token;
        this.tipo = tipo;
        this.rol = rol;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
}
