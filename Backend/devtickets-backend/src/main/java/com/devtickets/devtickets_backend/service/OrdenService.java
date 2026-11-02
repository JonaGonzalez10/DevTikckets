package com.devtickets.devtickets_backend.service;

import com.devtickets.devtickets_backend.dto.compra.*;
import com.devtickets.devtickets_backend.exception.ApiException;
import com.devtickets.devtickets_backend.model.*;
import com.devtickets.devtickets_backend.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdenService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final UsuarioRepository usuarioRepository;
    private final FuncionEventoRepository funcionEventoRepository;
    private final AsientoFuncionRepository asientoFuncionRepository;
    private final TipoEntradaRepository tipoEntradaRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final PagoRepository pagoRepository;

    @Value("${app.reservations.expiration-minutes:10}")
    private int minutosReserva;

    @Transactional
    public OrdenDto crear(String correo, CrearOrdenRequest req) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> {
                    log.warn("Creacion de reserva: usuario autenticado no existe en BD");
                    return ApiException.noEncontrado("Usuario no encontrado");
                });
        LocalDateTime ahora = ahora();
        log.info("Creando reserva: idUsuario={}, idFuncion={}, asientosSolicitados={}",
                usuario.getId(), req.idFuncion(), req.idsAsiento().size());

        FuncionEvento funcion = funcionEventoRepository.findDetalleById(req.idFuncion())
                .filter(f -> FuncionEvento.ESTADO_PROGRAMADO.equals(f.getEstado()))
                .orElseThrow(() -> {
                    log.warn("Creacion de reserva rechazada: funcion no encontrada idFuncion={}", req.idFuncion());
                    return ApiException.noEncontrado("Funcion no encontrada");
                });
        if (!funcion.ventaAbierta(ahora)) {
            log.warn("Creacion de reserva rechazada: venta cerrada idFuncion={}", funcion.getId());
            throw ApiException.conflicto("La venta de esta funcion no esta abierta");
        }

        ordenCompraRepository.expirarPendientes(ahora);
        asientoFuncionRepository.liberarReservasExpiradas();

        Set<Long> ids = new LinkedHashSet<>(req.idsAsiento());
        if (ids.size() != req.idsAsiento().size()) {
            throw ApiException.solicitudInvalida("No se puede incluir el mismo asiento mas de una vez");
        }
        List<AsientoFuncion> asientos = asientoFuncionRepository.findParaCompra(ids, funcion.getId());
        if (asientos.size() != ids.size()) {
            log.warn("Creacion de reserva rechazada: asientos ajenos a la funcion idFuncion={}", funcion.getId());
            throw ApiException.solicitudInvalida("Hay asientos que no pertenecen a la funcion");
        }
        for (AsientoFuncion asiento : asientos) {
            TipoEntrada tipo = asiento.getTipoEntrada();
            if (!disponible(asiento, ahora)) {
                log.warn("Creacion de reserva rechazada: asiento no disponible idAsiento={}", asiento.getId());
                throw ApiException.conflicto("El asiento " + etiqueta(asiento) + " ya no esta disponible");
            }
            if (!TipoEntrada.ESTADO_ACTIVO.equals(tipo.getEstado()) || !tipo.ventaAbierta(ahora)) {
                log.warn("Creacion de reserva rechazada: venta cerrada idTipoEntrada={}", tipo.getId());
                throw ApiException.conflicto("La venta del tipo de entrada " + tipo.getNombre() + " no esta abierta");
            }
        }

        Set<String> monedas = new HashSet<>();
        asientos.forEach(asiento -> monedas.add(asiento.getTipoEntrada().getMoneda()));
        if (monedas.size() > 1) {
            log.warn("Creacion de reserva rechazada: monedas mezcladas idFuncion={}", funcion.getId());
            throw ApiException.solicitudInvalida("Los asientos deben compartir la misma moneda");
        }

        Map<Long, List<AsientoFuncion>> porTipo = agruparPorTipo(asientos);
        OrdenCompra orden = new OrdenCompra();
        orden.setUsuario(usuario);
        orden.setNumeroOrden("ORD-" + FECHA.format(ahora) + "-" + hex(8));
        orden.setEstado(OrdenCompra.ESTADO_PENDIENTE);
        orden.setMoneda(monedas.iterator().next());
        orden.setCreadoEn(ahora);
        orden.setExpiraEn(ahora.plusMinutes(minutosReserva));

        BigDecimal total = BigDecimal.ZERO;
        for (List<AsientoFuncion> grupo : porTipo.values()) {
            TipoEntrada tipo = grupo.get(0).getTipoEntrada();
            DetalleOrden detalle = new DetalleOrden();
            detalle.setTipoEntrada(tipo);
            detalle.setCantidad(grupo.size());
            detalle.setPrecioUnitario(tipo.getPrecio());
            BigDecimal linea = tipo.getPrecio().multiply(BigDecimal.valueOf(grupo.size()));
            detalle.setTotalLinea(linea);
            total = total.add(linea);
            orden.agregarDetalle(detalle);
        }
        orden.setMontoTotal(total);
        ordenCompraRepository.saveAndFlush(orden);

        if (asientoFuncionRepository.reservarDisponibles(ids, funcion.getId(), orden.getId()) != ids.size()) {
            log.warn("Creacion de reserva rechazada: conflicto al reservar asientos idFuncion={}", funcion.getId());
            throw ApiException.conflicto("Alguno de los asientos acaba de ser reservado, intenta de nuevo");
        }

        Pago pago = new Pago();
        pago.setOrden(orden);
        pago.setProveedor(Pago.PROVEEDOR_SIMULADO);
        pago.setEstado(Pago.ESTADO_PENDIENTE);
        pago.setMonto(total);
        pago.setMoneda(orden.getMoneda());
        pago.setCreadoEn(ahora);
        pagoRepository.save(pago);

        log.info("Reserva creada: idOrden={}, numeroOrden={}, asientos={}, expiraEn={}",
                orden.getId(), orden.getNumeroOrden(), ids.size(), orden.getExpiraEn());
        return aDto(orden, funcion, null);
    }

    @Transactional
    public OrdenDto confirmarPago(String correo, Long idOrden, ConfirmarPagoRequest req) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> ApiException.noEncontrado("Usuario no encontrado"));
        OrdenCompra orden = ordenCompraRepository.findById(idOrden)
                .filter(o -> o.getUsuario().getId().equals(usuario.getId()))
                .orElseThrow(() -> ApiException.noEncontrado("Orden no encontrada"));

        LocalDateTime ahora = ahora();
        if (!OrdenCompra.ESTADO_PENDIENTE.equals(orden.getEstado())) {
            throw ApiException.conflicto("La orden no tiene un pago pendiente");
        }
        if (orden.getExpiraEn() == null || !orden.getExpiraEn().isAfter(ahora)) {
            throw ApiException.conflicto("La reserva expiro; selecciona nuevamente tus asientos");
        }

        List<AsientoFuncion> asientos = asientoFuncionRepository.findByReservaId(orden.getId());
        int cantidadEsperada = orden.getDetalles().stream().mapToInt(DetalleOrden::getCantidad).sum();
        if (asientos.size() != cantidadEsperada) {
            throw ApiException.conflicto("La reserva ya no esta disponible; selecciona nuevamente tus asientos");
        }

        Map<Long, List<AsientoFuncion>> porTipo = agruparPorTipo(asientos);
        for (Map.Entry<Long, List<AsientoFuncion>> grupo : porTipo.entrySet()) {
            if (tipoEntradaRepository.incrementarVendidas(grupo.getKey(), grupo.getValue().size()) != 1) {
                throw ApiException.conflicto("No hay cupo suficiente para completar la compra");
            }
        }
        if (asientoFuncionRepository.marcarVendidos(orden.getId()) != cantidadEsperada) {
            throw ApiException.conflicto("La reserva ya no esta disponible; selecciona nuevamente tus asientos");
        }

        Map<Long, DetalleOrden> detallesPorTipo = new HashMap<>();
        orden.getDetalles().forEach(detalle -> detallesPorTipo.put(detalle.getTipoEntrada().getId(), detalle));
        for (AsientoFuncion asiento : asientos) {
            DetalleOrden detalle = detallesPorTipo.get(asiento.getTipoEntrada().getId());
            if (detalle == null) {
                throw ApiException.conflicto("No se pudo asociar el asiento con la orden");
            }
            Entrada entrada = new Entrada();
            entrada.setAsiento(asiento);
            entrada.setCodigoEntrada("DT-" + hex(16));
            entrada.setEstado(Entrada.ESTADO_VALIDA);
            entrada.setEmitidaEn(ahora);
            detalle.agregarEntrada(entrada);
        }

        orden.setEstado(OrdenCompra.ESTADO_PAGADA);
        orden.setPagadoEn(ahora);
        ordenCompraRepository.save(orden);

        Pago pago = pagoRepository.findFirstByOrdenIdOrderByIdDesc(orden.getId())
                .orElseThrow(() -> ApiException.conflicto("No se encontro el pago pendiente de la orden"));
        pago.setReferenciaProveedor("SIM-" + UUID.randomUUID());
        pago.setEstado(Pago.ESTADO_APROBADO);
        pago.setPagadoEn(ahora);
        pagoRepository.save(pago);

        FuncionEvento funcion = orden.getDetalles().get(0).getTipoEntrada().getFuncion();
        log.info("Pago simulado aprobado: idOrden={}, numeroOrden={}, entradas={}",
                orden.getId(), orden.getNumeroOrden(), cantidadEsperada);
        return aDto(orden, funcion, pago.getReferenciaProveedor());
    }

    @Transactional(readOnly = true)
    public List<OrdenDto> listar(String correo) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> {
                    log.warn("Listado de ordenes: usuario autenticado no existe en BD");
                    return ApiException.noEncontrado("Usuario no encontrado");
                });
        log.debug("Listando ordenes: idUsuario={}", usuario.getId());
        return ordenCompraRepository.findByUsuarioIdOrderByCreadoEnDesc(usuario.getId()).stream()
                .map(orden -> {
                    FuncionEvento funcion = orden.getDetalles().get(0).getTipoEntrada().getFuncion();
                    String referencia = pagoRepository.findFirstByOrdenIdOrderByIdDesc(orden.getId())
                            .map(Pago::getReferenciaProveedor).orElse(null);
                    return aDto(orden, funcion, referencia);
                })
                .toList();
    }

    private OrdenDto aDto(OrdenCompra orden, FuncionEvento funcion, String referencia) {
        List<EntradaDto> entradas = orden.getDetalles().stream()
                .flatMap(detalle -> detalle.getEntradas().stream())
                .map(entrada -> new EntradaDto(entrada.getAsiento().getId(), entrada.getCodigoEntrada(), entrada.getAsiento().getSeccion(),
                        entrada.getAsiento().getFila(), entrada.getAsiento().getNumero(),
                        entrada.getAsiento().getTipoEntrada().getNombre(), entrada.getEstado()))
                .toList();
        return new OrdenDto(orden.getId(), orden.getNumeroOrden(), orden.getEstado(), funcion.getId(),
                funcion.getEvento().getNombre(),
                funcion.getFechaInicio(), funcion.getRecinto().getNombre(), orden.getMontoTotal(), orden.getMoneda(),
                orden.getPagadoEn(), referencia, entradas, orden.getExpiraEn());
    }

    private static Map<Long, List<AsientoFuncion>> agruparPorTipo(List<AsientoFuncion> asientos) {
        Map<Long, List<AsientoFuncion>> porTipo = new LinkedHashMap<>();
        asientos.forEach(asiento ->
                porTipo.computeIfAbsent(asiento.getTipoEntrada().getId(), ignored -> new ArrayList<>()).add(asiento));
        return porTipo;
    }

    private static boolean disponible(AsientoFuncion asiento, LocalDateTime ahora) {
        return AsientoFuncion.ESTADO_DISPONIBLE.equals(asiento.getEstado())
                || (AsientoFuncion.ESTADO_RESERVADO.equals(asiento.getEstado())
                        && asiento.getReserva() != null
                        && asiento.getReserva().getExpiraEn() != null
                        && !asiento.getReserva().getExpiraEn().isAfter(ahora));
    }

    private static String etiqueta(AsientoFuncion asiento) {
        return asiento.getSeccion() + " " + asiento.getFila() + asiento.getNumero();
    }

    private static LocalDateTime ahora() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    private static String hex(int n) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, n).toUpperCase();
    }
}
