package com.ventaslibros.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoSaldoResponse {
    private Long id;
    private String tipo;
    private BigDecimal monto;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoNuevo;
    private String metodo;
    private String estado;
    private String codigoOperacion;
    private String referencia;
    private String descripcion;
    private LocalDateTime fecha;

    public MovimientoSaldoResponse() {}

    public MovimientoSaldoResponse(Long id, String tipo, BigDecimal monto, BigDecimal saldoAnterior,
                                   BigDecimal saldoNuevo, String metodo, String estado, String codigoOperacion,
                                   String referencia, String descripcion, LocalDateTime fecha) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoAnterior = saldoAnterior;
        this.saldoNuevo = saldoNuevo;
        this.metodo = metodo;
        this.estado = estado;
        this.codigoOperacion = codigoOperacion;
        this.referencia = referencia;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }
    public BigDecimal getSaldoNuevo() { return saldoNuevo; }
    public void setSaldoNuevo(BigDecimal saldoNuevo) { this.saldoNuevo = saldoNuevo; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCodigoOperacion() { return codigoOperacion; }
    public void setCodigoOperacion(String codigoOperacion) { this.codigoOperacion = codigoOperacion; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
