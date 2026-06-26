package com.ventaslibros.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class VentaRequest {
    @NotNull
    private Long clienteId;

    @Valid
    @NotEmpty
    private List<DetalleVentaRequest> detalles;

    private String metodoPago;
    private String puntoRecojoId;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public List<DetalleVentaRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaRequest> detalles) { this.detalles = detalles; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getPuntoRecojoId() { return puntoRecojoId; }
    public void setPuntoRecojoId(String puntoRecojoId) { this.puntoRecojoId = puntoRecojoId; }
}
