package com.ventaslibros.application.dto.response;

import java.math.BigDecimal;

public class VentaValidacionResponse {
    private Boolean valida;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String mensaje;

    public VentaValidacionResponse() {}

    public VentaValidacionResponse(Boolean valida, BigDecimal subtotal, BigDecimal igv, BigDecimal total, String mensaje) {
        this.valida = valida;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
        this.mensaje = mensaje;
    }

    public Boolean getValida() { return valida; }
    public void setValida(Boolean valida) { this.valida = valida; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
