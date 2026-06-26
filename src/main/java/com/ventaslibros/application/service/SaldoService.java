package com.ventaslibros.application.service;

import com.ventaslibros.application.dto.request.RecargaRequest;
import com.ventaslibros.application.dto.response.MovimientoSaldoResponse;
import com.ventaslibros.application.dto.response.RecargaSaldoResponse;
import com.ventaslibros.application.dto.response.SaldoResponse;
import com.ventaslibros.application.exception.ReglaNegocioException;
import com.ventaslibros.domain.enums.EstadoMovimiento;
import com.ventaslibros.domain.enums.MetodoRecarga;
import com.ventaslibros.domain.enums.TipoMovimiento;
import com.ventaslibros.infrastructure.persistence.entity.ClienteEntity;
import com.ventaslibros.infrastructure.persistence.entity.MovimientoSaldoEntity;
import com.ventaslibros.infrastructure.persistence.repository.ClienteJpaRepository;
import com.ventaslibros.infrastructure.persistence.repository.MovimientoSaldoJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class SaldoService {

    private static final BigDecimal MONTO_MINIMO = new BigDecimal("1.00");
    private static final BigDecimal MONTO_MAXIMO = new BigDecimal("2000.00");

    private final ClienteJpaRepository clienteRepository;
    private final MovimientoSaldoJpaRepository movimientoRepository;

    public SaldoService(ClienteJpaRepository clienteRepository, MovimientoSaldoJpaRepository movimientoRepository) {
        this.clienteRepository = clienteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public RecargaSaldoResponse recargar(Long clienteId, RecargaRequest request) {
        ClienteEntity cliente = clienteRepository.findByIdAndEliminadoFalse(clienteId)
                .orElseThrow(() -> new ReglaNegocioException("Cliente no encontrado"));

        BigDecimal monto = normalizarMonto(request.getMonto());
        BigDecimal saldoAnterior = saldoSeguro(cliente.getSaldo());
        BigDecimal saldoNuevo = saldoAnterior.add(monto).setScale(2, RoundingMode.HALF_UP);
        LocalDateTime fecha = LocalDateTime.now();
        String codigoOperacion = generarCodigoOperacion("REC");

        cliente.setSaldo(saldoNuevo);
        clienteRepository.save(cliente);

        MovimientoSaldoEntity movimiento = new MovimientoSaldoEntity();
        movimiento.setCliente(cliente);
        movimiento.setTipo(TipoMovimiento.RECARGA);
        movimiento.setMonto(monto);
        movimiento.setSaldoAnterior(saldoAnterior);
        movimiento.setSaldoNuevo(saldoNuevo);
        movimiento.setMetodo(MetodoRecarga.TARJETA_SIMULADA);
        movimiento.setEstado(EstadoMovimiento.APROBADA);
        movimiento.setCodigoOperacion(codigoOperacion);
        movimiento.setReferencia("RECARGA_APP");
        movimiento.setDescripcion("Recarga de saldo desde APK");
        movimiento.setFecha(fecha);
        movimientoRepository.save(movimiento);

        return new RecargaSaldoResponse(
                codigoOperacion,
                monto,
                saldoAnterior,
                saldoNuevo,
                movimiento.getMetodo().name(),
                movimiento.getEstado().name(),
                fecha,
                "Recarga realizada correctamente"
        );
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerSaldo(Long clienteId) {
        ClienteEntity cliente = clienteRepository.findByIdAndEliminadoFalse(clienteId)
                .orElseThrow(() -> new ReglaNegocioException("Cliente no encontrado"));
        return saldoSeguro(cliente.getSaldo());
    }

    @Transactional(readOnly = true)
    public SaldoResponse obtenerSaldoResponse(Long clienteId) {
        return new SaldoResponse(obtenerSaldo(clienteId));
    }

    @Transactional(readOnly = true)
    public List<MovimientoSaldoResponse> listarRecargas(Long clienteId, String fechaInicio, String fechaFin,
                                                        String ordenFecha, String ordenMonto) {
        LocalDate desde = parseFechaOpcional(fechaInicio, "fechaInicio");
        LocalDate hasta = parseFechaOpcional(fechaFin, "fechaFin");

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha de inicio no puede ser mayor que la fecha final");
        }

        Comparator<MovimientoSaldoResponse> comparador = construirComparador(ordenFecha, ordenMonto);

        return movimientoRepository.findByClienteIdAndTipoOrderByFechaDesc(clienteId, TipoMovimiento.RECARGA)
                .stream()
                .map(this::toMovimientoResponse)
                .filter(mov -> dentroDelRango(mov.getFecha(), desde, hasta))
                .sorted(comparador)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoSaldoResponse> listarMovimientos(Long clienteId) {
        return movimientoRepository.findByClienteIdOrderByFechaDesc(clienteId)
                .stream()
                .map(this::toMovimientoResponse)
                .toList();
    }

    @Transactional
    public void deducirSaldo(Long clienteId, BigDecimal monto, String descripcion) {
        ClienteEntity cliente = clienteRepository.findByIdAndEliminadoFalse(clienteId)
                .orElseThrow(() -> new ReglaNegocioException("Cliente no encontrado"));

        BigDecimal montoNormalizado = normalizarMonto(monto);
        BigDecimal saldoAnterior = saldoSeguro(cliente.getSaldo());
        if (saldoAnterior.compareTo(montoNormalizado) < 0) {
            throw new ReglaNegocioException("Saldo insuficiente");
        }

        BigDecimal saldoNuevo = saldoAnterior.subtract(montoNormalizado).setScale(2, RoundingMode.HALF_UP);
        cliente.setSaldo(saldoNuevo);
        clienteRepository.save(cliente);

        MovimientoSaldoEntity mov = new MovimientoSaldoEntity();
        mov.setCliente(cliente);
        mov.setTipo(TipoMovimiento.COMPRA);
        mov.setMonto(montoNormalizado);
        mov.setSaldoAnterior(saldoAnterior);
        mov.setSaldoNuevo(saldoNuevo);
        mov.setMetodo(MetodoRecarga.SALDO);
        mov.setEstado(EstadoMovimiento.APROBADA);
        mov.setCodigoOperacion(generarCodigoOperacion("COM"));
        mov.setReferencia("COMPRA");
        mov.setDescripcion(descripcion);
        mov.setFecha(LocalDateTime.now());
        movimientoRepository.save(mov);
    }

    @Transactional
    public void devolverSaldo(Long clienteId, BigDecimal monto, String descripcion) {
        ClienteEntity cliente = clienteRepository.findByIdAndEliminadoFalse(clienteId)
                .orElseThrow(() -> new ReglaNegocioException("Cliente no encontrado"));

        BigDecimal montoNormalizado = normalizarMonto(monto);
        BigDecimal saldoAnterior = saldoSeguro(cliente.getSaldo());
        BigDecimal saldoNuevo = saldoAnterior.add(montoNormalizado).setScale(2, RoundingMode.HALF_UP);
        cliente.setSaldo(saldoNuevo);
        clienteRepository.save(cliente);

        MovimientoSaldoEntity mov = new MovimientoSaldoEntity();
        mov.setCliente(cliente);
        mov.setTipo(TipoMovimiento.DEVOLUCION);
        mov.setMonto(montoNormalizado);
        mov.setSaldoAnterior(saldoAnterior);
        mov.setSaldoNuevo(saldoNuevo);
        mov.setMetodo(MetodoRecarga.SALDO);
        mov.setEstado(EstadoMovimiento.APROBADA);
        mov.setCodigoOperacion(generarCodigoOperacion("DEV"));
        mov.setReferencia("DEVOLUCION");
        mov.setDescripcion(descripcion);
        mov.setFecha(LocalDateTime.now());
        movimientoRepository.save(mov);
    }

    private BigDecimal normalizarMonto(BigDecimal monto) {
        if (monto == null) {
            throw new ReglaNegocioException("Ingresa un monto válido");
        }
        BigDecimal valor = monto.setScale(2, RoundingMode.HALF_UP);
        if (valor.compareTo(MONTO_MINIMO) < 0) {
            throw new ReglaNegocioException("El monto mínimo de recarga es S/ 1.00");
        }
        if (valor.compareTo(MONTO_MAXIMO) > 0) {
            throw new ReglaNegocioException("El monto máximo permitido por operación es S/ 2000.00");
        }
        return valor;
    }

    private BigDecimal saldoSeguro(BigDecimal saldo) {
        return (saldo == null ? BigDecimal.ZERO : saldo).setScale(2, RoundingMode.HALF_UP);
    }

    private MovimientoSaldoResponse toMovimientoResponse(MovimientoSaldoEntity mov) {
        return new MovimientoSaldoResponse(
                mov.getId(),
                mov.getTipo() == null ? null : mov.getTipo().name(),
                mov.getMonto(),
                mov.getSaldoAnterior(),
                mov.getSaldoNuevo(),
                mov.getMetodo() == null ? null : mov.getMetodo().name(),
                mov.getEstado() == null ? null : mov.getEstado().name(),
                mov.getCodigoOperacion(),
                mov.getReferencia(),
                mov.getDescripcion(),
                mov.getFecha()
        );
    }

    private LocalDate parseFechaOpcional(String valor, String campo) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException ex) {
            throw new ReglaNegocioException("La " + campo + " debe tener formato YYYY-MM-DD");
        }
    }

    private boolean dentroDelRango(LocalDateTime fecha, LocalDate desde, LocalDate hasta) {
        if (fecha == null) return false;
        LocalDate fechaMovimiento = fecha.toLocalDate();
        if (desde != null && fechaMovimiento.isBefore(desde)) return false;
        return hasta == null || !fechaMovimiento.isAfter(hasta);
    }

    private Comparator<MovimientoSaldoResponse> construirComparador(String ordenFecha, String ordenMonto) {
        String monto = ordenMonto == null ? "" : ordenMonto.trim().toUpperCase(Locale.ROOT);
        String fecha = ordenFecha == null ? "RECIENTES" : ordenFecha.trim().toUpperCase(Locale.ROOT);

        if ("MAYOR_MENOR".equals(monto)) {
            return Comparator.comparing(
                    MovimientoSaldoResponse::getMonto,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed();
        }
        if ("MENOR_MAYOR".equals(monto)) {
            return Comparator.comparing(
                    MovimientoSaldoResponse::getMonto,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        }
        if ("ANTIGUAS".equals(fecha)) {
            return Comparator.comparing(
                    MovimientoSaldoResponse::getFecha,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        }
        return Comparator.comparing(
                MovimientoSaldoResponse::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed();
    }

    private String generarCodigoOperacion(String prefijo) {
        return prefijo + "-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + codigoCorto();
    }

    private String codigoCorto() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
