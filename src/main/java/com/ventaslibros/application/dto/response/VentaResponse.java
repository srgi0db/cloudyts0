package com.ventaslibros.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentaResponse {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private LocalDateTime fechaVenta;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String estado;
    private String codigoVerificacion;
    private String puntoRecojoId;
    private String puntoRecojoNombre;
    private String metodoPago;
    private Boolean fueModificado = false;
    private List<DetalleVentaResponse> detalles;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCodigoVerificacion() { return codigoVerificacion; }
    public void setCodigoVerificacion(String codigoVerificacion) { this.codigoVerificacion = codigoVerificacion; }
    public String getPuntoRecojoId() { return puntoRecojoId; }
    public void setPuntoRecojoId(String puntoRecojoId) { this.puntoRecojoId = puntoRecojoId; }
    public String getPuntoRecojoNombre() { return puntoRecojoNombre; }
    public void setPuntoRecojoNombre(String puntoRecojoNombre) { this.puntoRecojoNombre = puntoRecojoNombre; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public Boolean getFueModificado() { return fueModificado; }
    public void setFueModificado(Boolean fueModificado) { this.fueModificado = fueModificado; }
    public List<DetalleVentaResponse> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaResponse> detalles) { this.detalles = detalles; }
}
