package com.ventaslibros.application.service;

import com.ventaslibros.application.dto.request.DetalleVentaRequest;
import com.ventaslibros.application.dto.request.VentaRequest;
import com.ventaslibros.application.dto.response.DetalleVentaResponse;
import com.ventaslibros.application.dto.response.PuntoRecojoResponse;
import com.ventaslibros.application.dto.response.VentaResponse;
import com.ventaslibros.application.dto.response.VentaValidacionResponse;
import com.ventaslibros.application.exception.RecursoNoEncontradoException;
import com.ventaslibros.application.exception.ReglaNegocioException;
import com.ventaslibros.domain.enums.EstadoRegistro;
import com.ventaslibros.domain.enums.EstadoVenta;
import com.ventaslibros.infrastructure.persistence.entity.ClienteEntity;
import com.ventaslibros.infrastructure.persistence.entity.DetalleVentaEntity;
import com.ventaslibros.infrastructure.persistence.entity.LibroEntity;
import com.ventaslibros.infrastructure.persistence.entity.VentaEntity;
import com.ventaslibros.infrastructure.persistence.repository.ClienteJpaRepository;
import com.ventaslibros.infrastructure.persistence.repository.LibroJpaRepository;
import com.ventaslibros.infrastructure.persistence.repository.VentaJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class VentaService {
    private static final BigDecimal IGV = new BigDecimal("0.18");

    private static final List<PuntoRecojoResponse> PUNTOS_RECOJO = List.of(
            new PuntoRecojoResponse("TIENDA_CENTRAL", "Tienda Central", "Av. Principal 123 - Lima", "Lunes a sábado de 9:00 a.m. a 8:00 p.m."),
            new PuntoRecojoResponse("TIENDA_NORTE", "Tienda Norte", "Av. Los Olivos 456 - Lima", "Lunes a sábado de 10:00 a.m. a 7:00 p.m."),
            new PuntoRecojoResponse("TIENDA_SUR", "Tienda Sur", "Av. Cultura 789 - Lima", "Lunes a viernes de 9:00 a.m. a 6:00 p.m.")
    );

    private final VentaJpaRepository ventaRepository;
    private final ClienteJpaRepository clienteRepository;
    private final LibroJpaRepository libroRepository;
    private final SaldoService saldoService;

    public VentaService(
            VentaJpaRepository ventaRepository,
            ClienteJpaRepository clienteRepository,
            LibroJpaRepository libroRepository,
            SaldoService saldoService) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.libroRepository = libroRepository;
        this.saldoService = saldoService;
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {
        return ventaRepository.findAllByOrderByFechaVentaDesc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse buscarPorId(Long id) {
        return toResponse(ventaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada")));
    }

    @Transactional(readOnly = true)
    public List<PuntoRecojoResponse> listarPuntosRecojo() {
        return PUNTOS_RECOJO;
    }

    @Transactional(readOnly = true)
    public VentaValidacionResponse validarCompra(VentaRequest request, String emailUsuario) {
        ClienteEntity cliente = obtenerClienteAutenticado(request, emailUsuario);
        TotalesCompra totales = calcularYValidarTotales(request, cliente);
        return new VentaValidacionResponse(true, totales.subtotal(), totales.igv(), totales.total(), "Compra validada correctamente");
    }

    public VentaResponse confirmarCompraRecojo(VentaRequest request, String emailUsuario) {
        ClienteEntity cliente = obtenerClienteAutenticado(request, emailUsuario);
        PuntoRecojoResponse punto = obtenerPuntoRecojo(request.getPuntoRecojoId());
        TotalesCompra totales = calcularYValidarTotales(request, cliente);

        VentaEntity venta = new VentaEntity();
        venta.setCliente(cliente);
        venta.setSubtotal(totales.subtotal());
        venta.setIgv(totales.igv());
        venta.setTotal(totales.total());
        venta.setEstado(EstadoVenta.COMPLETADA);
        venta.setMetodoPago("SALDO");
        venta.setCodigoVerificacion(generarCodigoVerificacion());
        venta.setPuntoRecojoId(punto.getId());
        venta.setPuntoRecojoNombre(punto.getNombre());

        List<DetalleVentaEntity> detalles = new ArrayList<>();
        for (DetalleVentaRequest item : request.getDetalles()) {
            LibroEntity libro = libroRepository.findByIdAndEliminadoFalse(item.getLibroId())
                    .orElseThrow(() -> new ReglaNegocioException("El libro seleccionado ya no está disponible"));
            if (libro.getStock() < item.getCantidad()) {
                throw new ReglaNegocioException("Cantidad no disponible para este libro");
            }
            BigDecimal subtotal = libro.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())).setScale(2, RoundingMode.HALF_UP);
            DetalleVentaEntity detalle = new DetalleVentaEntity();
            detalle.setVenta(venta);
            detalle.setLibro(libro);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(libro.getPrecioUnitario());
            detalle.setSubtotal(subtotal);
            detalles.add(detalle);
            libro.setStock(libro.getStock() - item.getCantidad());
            libroRepository.save(libro);
        }

        saldoService.deducirSaldo(cliente.getId(), totales.total(), "Compra de libros - " + venta.getCodigoVerificacion());
        venta.setDetalles(detalles);
        return toResponse(ventaRepository.save(venta));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listarMisCompras(String emailUsuario) {
        return ventaRepository.findByClienteEmailAndClienteEliminadoFalseOrderByFechaVentaDesc(emailUsuario)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse obtenerMiCompra(Long id, String emailUsuario) {
        return toResponse(ventaRepository.findByIdAndClienteEmailAndClienteEliminadoFalse(id, emailUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada")));
    }

    public VentaResponse cancelarMiCompra(Long id, String emailUsuario) {
        throw new ReglaNegocioException("Las compras confirmadas no se cancelan desde la aplicación");
    }

    public VentaResponse registrar(VentaRequest request) {
        ClienteEntity cliente = clienteRepository.findByIdAndEliminadoFalse(request.getClienteId()).orElseThrow(() -> new ReglaNegocioException("El cliente no existe"));
        VentaEntity venta = new VentaEntity();
        venta.setCliente(cliente);
        List<DetalleVentaEntity> detalles = new ArrayList<>();
        BigDecimal subtotalGeneral = BigDecimal.ZERO;
        for (DetalleVentaRequest item : request.getDetalles()) {
            LibroEntity libro = libroRepository.findByIdAndEliminadoFalse(item.getLibroId()).orElseThrow(() -> new ReglaNegocioException("El libro no existe: " + item.getLibroId()));
            if (libro.getEstado() != EstadoRegistro.ACTIVO) throw new ReglaNegocioException("El libro está inactivo: " + libro.getTitulo());
            if (libro.getStock() < item.getCantidad()) throw new ReglaNegocioException("Cantidad no disponible para este libro");
            BigDecimal subtotal = libro.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())).setScale(2, RoundingMode.HALF_UP);
            DetalleVentaEntity detalle = new DetalleVentaEntity();
            detalle.setVenta(venta);
            detalle.setLibro(libro);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(libro.getPrecioUnitario());
            detalle.setSubtotal(subtotal);
            detalles.add(detalle);
            subtotalGeneral = subtotalGeneral.add(subtotal);
            libro.setStock(libro.getStock() - item.getCantidad());
            libroRepository.save(libro);
        }

        if ("SALDO".equalsIgnoreCase(request.getMetodoPago())) {
            BigDecimal total = calcularTotal(subtotalGeneral);
            saldoService.deducirSaldo(request.getClienteId(), total, "Compra de libros");
        }

        BigDecimal igv = subtotalGeneral.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        venta.setSubtotal(subtotalGeneral);
        venta.setIgv(igv);
        venta.setTotal(subtotalGeneral.add(igv));
        venta.setEstado(EstadoVenta.COMPLETADA);
        venta.setMetodoPago(request.getMetodoPago());
        venta.setDetalles(detalles);
        return toResponse(ventaRepository.save(venta));
    }

    private ClienteEntity obtenerClienteAutenticado(VentaRequest request, String emailUsuario) {
        ClienteEntity cliente = clienteRepository.findByEmailAndEliminadoFalse(emailUsuario)
                .orElseThrow(() -> new ReglaNegocioException("Cliente autenticado no encontrado"));
        if (request.getClienteId() != null && !cliente.getId().equals(request.getClienteId())) {
            throw new ReglaNegocioException("La compra no corresponde al cliente autenticado");
        }
        return cliente;
    }

    private TotalesCompra calcularYValidarTotales(VentaRequest request, ClienteEntity cliente) {
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new ReglaNegocioException("El carrito está vacío");
        }
        BigDecimal subtotalGeneral = BigDecimal.ZERO;
        for (DetalleVentaRequest item : request.getDetalles()) {
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new ReglaNegocioException("Cantidad inválida");
            }
            LibroEntity libro = libroRepository.findByIdAndEliminadoFalse(item.getLibroId())
                    .orElseThrow(() -> new ReglaNegocioException("El libro seleccionado ya no está disponible"));
            if (libro.getEstado() != EstadoRegistro.ACTIVO || libro.getStock() <= 0) {
                throw new ReglaNegocioException("El libro seleccionado no está disponible");
            }
            if (libro.getStock() < item.getCantidad()) {
                throw new ReglaNegocioException("Cantidad no disponible para este libro");
            }
            BigDecimal subtotal = libro.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())).setScale(2, RoundingMode.HALF_UP);
            subtotalGeneral = subtotalGeneral.add(subtotal);
        }
        BigDecimal igv = subtotalGeneral.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotalGeneral.add(igv).setScale(2, RoundingMode.HALF_UP);
        if (cliente.getSaldo().compareTo(total) < 0) {
            throw new ReglaNegocioException("Saldo insuficiente");
        }
        return new TotalesCompra(subtotalGeneral.setScale(2, RoundingMode.HALF_UP), igv, total);
    }

    private PuntoRecojoResponse obtenerPuntoRecojo(String puntoRecojoId) {
        if (puntoRecojoId == null || puntoRecojoId.isBlank()) {
            throw new ReglaNegocioException("Selecciona un punto de recojo");
        }
        return PUNTOS_RECOJO.stream()
                .filter(p -> p.getId().equalsIgnoreCase(puntoRecojoId))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("Punto de recojo inválido"));
    }

    private String generarCodigoVerificacion() {
        String fecha = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String corto = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return "TL-" + fecha + "-" + corto;
    }

    private BigDecimal calcularTotal(BigDecimal subtotalGeneral) {
        BigDecimal igv = subtotalGeneral.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        return subtotalGeneral.add(igv);
    }

    public VentaResponse anular(Long id) {
        throw new ReglaNegocioException("Las compras confirmadas no se anulan en este flujo");
    }

    private VentaResponse toResponse(VentaEntity v) {
        VentaResponse r = new VentaResponse();
        r.setId(v.getId());
        r.setClienteId(v.getCliente().getId());
        r.setClienteNombre(v.getCliente().getNombres() + " " + v.getCliente().getApellidos());
        r.setClienteEmail(v.getCliente().getEmail());
        r.setFueModificado(false);
        r.setFechaVenta(v.getFechaVenta());
        r.setSubtotal(v.getSubtotal());
        r.setIgv(v.getIgv());
        r.setTotal(v.getTotal());
        r.setEstado(v.getEstado() != null ? v.getEstado().name() : EstadoVenta.COMPLETADA.name());
        r.setCodigoVerificacion(v.getCodigoVerificacion());
        r.setPuntoRecojoId(v.getPuntoRecojoId());
        r.setPuntoRecojoNombre(v.getPuntoRecojoNombre());
        r.setMetodoPago(v.getMetodoPago());
        r.setDetalles(v.getDetalles().stream().map(d -> {
            DetalleVentaResponse x = new DetalleVentaResponse();
            x.setLibroId(d.getLibro().getId());
            x.setLibroTitulo(d.getLibro().getTitulo());
            x.setCantidad(d.getCantidad());
            x.setPrecioUnitario(d.getPrecioUnitario());
            x.setSubtotal(d.getSubtotal());
            return x;
        }).toList());
        return r;
    }

    private record TotalesCompra(BigDecimal subtotal, BigDecimal igv, BigDecimal total) {}
}
