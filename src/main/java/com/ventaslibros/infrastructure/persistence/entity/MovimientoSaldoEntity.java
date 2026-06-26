package com.ventaslibros.infrastructure.persistence.entity;

import com.ventaslibros.domain.enums.EstadoMovimiento;
import com.ventaslibros.domain.enums.MetodoRecarga;
import com.ventaslibros.domain.enums.TipoMovimiento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_saldo")
public class MovimientoSaldoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "saldo_anterior", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_nuevo", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoNuevo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoRecarga metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMovimiento estado = EstadoMovimiento.APROBADA;

    @Column(name = "codigo_operacion", length = 40)
    private String codigoOperacion;

    @Column(length = 80)
    private String referencia;

    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ClienteEntity getCliente() { return cliente; }
    public void setCliente(ClienteEntity cliente) { this.cliente = cliente; }
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }
    public BigDecimal getSaldoNuevo() { return saldoNuevo; }
    public void setSaldoNuevo(BigDecimal saldoNuevo) { this.saldoNuevo = saldoNuevo; }
    public MetodoRecarga getMetodo() { return metodo; }
    public void setMetodo(MetodoRecarga metodo) { this.metodo = metodo; }
    public EstadoMovimiento getEstado() { return estado; }
    public void setEstado(EstadoMovimiento estado) { this.estado = estado; }
    public String getCodigoOperacion() { return codigoOperacion; }
    public void setCodigoOperacion(String codigoOperacion) { this.codigoOperacion = codigoOperacion; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
