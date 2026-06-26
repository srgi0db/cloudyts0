package com.ventaslibros.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecargaSaldoResponse {
    private String codigoOperacion;
    private BigDecimal monto;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoNuevo;
    private String metodo;
    private String estado;
    private LocalDateTime fecha;
    private String mensaje;

    public RecargaSaldoResponse() {}

    public RecargaSaldoResponse(String codigoOperacion, BigDecimal monto, BigDecimal saldoAnterior,
                                BigDecimal saldoNuevo, String metodo, String estado, LocalDateTime fecha,
                                String mensaje) {
        this.codigoOperacion = codigoOperacion;
        this.monto = monto;
        this.saldoAnterior = saldoAnterior;
        this.saldoNuevo = saldoNuevo;
        this.metodo = metodo;
        this.estado = estado;
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    public String getCodigoOperacion() { return codigoOperacion; }
    public void setCodigoOperacion(String codigoOperacion) { this.codigoOperacion = codigoOperacion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }
    public BigDecimal getSaldoNuevo() { return saldoNuevo; }
    public void setSaldoNuevo(BigDecimal saldoNuevo) { this.saldoNuevo = saldoNuevo; }

    // Alias útil para clientes REST que usen el nombre saldoActual después de confirmar.
    public BigDecimal getSaldoActual() { return saldoNuevo; }

    // Alias útil si algún cliente REST usa saldoPosterior.
    public BigDecimal getSaldoPosterior() { return saldoNuevo; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
