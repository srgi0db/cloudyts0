package com.ventaslibros.application.dto.response;

public class PuntoRecojoResponse {
    private String id;
    private String nombre;
    private String direccion;
    private String horario;

    public PuntoRecojoResponse() {}

    public PuntoRecojoResponse(String id, String nombre, String direccion, String horario) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.horario = horario;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}
