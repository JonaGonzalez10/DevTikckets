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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public OrdenDto crear(String correo, CrearOrdenRequest req) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> ApiException.noEncontrado("Usuario no encontrado"));
        LocalDateTime ahora = LocalDateTime.now(ZoneOffset.UTC);

        FuncionEvento funcion = funcionEventoRepository.findDetalleById(req.idFuncion())
                .filter(f -> FuncionEvento.ESTADO_PROGRAMADO.equals(f.getEstado()))
                .orElseThrow(() -> ApiException.noEncontrado("Funcion no encontrada"));
        if (!funcion.ventaAbierta(ahora)) {
            throw ApiException.conflicto("La venta de esta funcion no esta abierta");
        }

        Set<Long> ids = new LinkedHashSet<>(req.idsAsiento());
        List<AsientoFuncion> asientos = asientoFuncionRepository.findParaCompra(ids, funcion.getId());
        if (asientos.size() != ids.size()) {
            throw ApiException.solicitudInvalida("Hay asientos que no pertenecen a la funcion");
        }
        for (AsientoFuncion a : asientos) {
            TipoEntrada t = a.getTipoEntrada();
            if (!AsientoFuncion.ESTADO_DISPONIBLE.equals(a.getEstado())) {
                throw ApiException.conflicto("El asiento " + etiqueta(a) + " ya no esta disponible");
            }
            if (!TipoEntrada.ESTADO_ACTIVO.equals(t.getEstado()) || !t.ventaAbierta(ahora)) {
                throw ApiException.conflicto("La venta del tipo de entrada " + t.getNombre() + " no esta abierta");
            }
        }

        Set<String> monedas = new HashSet<>();
        asientos.forEach(a -> monedas.add(a.getTipoEntrada().getMoneda()));
        if (monedas.size() > 1) {
            throw ApiException.solicitudInvalida("Los asientos deben compartir la misma moneda");
        }

        // Reserva atomica: si otro comprador gano la carrera, el conteo no coincide y se revierte todo.
        if (asientoFuncionRepository.marcarVendidos(ids) != ids.size()) {
            throw ApiException.conflicto("Alguno de los asientos acaba de ser vendido, intenta de nuevo");
        }

        Map<Long, List<AsientoFuncion>> porTipo = new LinkedHashMap<>();
        asientos.forEach(a -> porTipo.computeIfAbsent(a.getTipoEntrada().getId(), k -> new ArrayList<>()).add(a));

        OrdenCompra orden = new OrdenCompra();
        orden.setUsuario(usuario);
        orden.setNumeroOrden("ORD-" + FECHA.format(ahora) + "-" + hex(8));
        orden.setEstado(OrdenCompra.ESTADO_PAGADA);
        orden.setMoneda(monedas.iterator().next());
        orden.setCreadoEn(ahora);
        orden.setPagadoEn(ahora);

        BigDecimal total = BigDecimal.ZERO;
        for (List<AsientoFuncion> grupo : porTipo.values()) {
            TipoEntrada tipo = grupo.get(0).getTipoEntrada();
            if (tipoEntradaRepository.incrementarVendidas(tipo.getId(), grupo.size()) != 1) {
                throw ApiException.conflicto("No hay cupo suficiente para " + tipo.getNombre());
            }
            DetalleOrden detalle = new DetalleOrden();
            detalle.setTipoEntrada(tipo);
            detalle.setCantidad(grupo.size());
            detalle.setPrecioUnitario(tipo.getPrecio());
            BigDecimal linea = tipo.getPrecio().multiply(BigDecimal.valueOf(grupo.size()));
            detalle.setTotalLinea(linea);
            total = total.add(linea);
            for (AsientoFuncion a : grupo) {
                Entrada e = new Entrada();
                e.setAsiento(a);
                e.setCodigoEntrada("DT-" + hex(16));
                e.setEstado(Entrada.ESTADO_VALIDA);
                e.setEmitidaEn(ahora);
                detalle.agregarEntrada(e);
            }
            orden.agregarDetalle(detalle);
        }
        orden.setMontoTotal(total);
        ordenCompraRepository.save(orden);

        // Simulacion: se aprueba cualquier tarjeta valida en formato; los datos de tarjeta no se guardan.
        Pago pago = new Pago();
        pago.setOrden(orden);
        pago.setProveedor(Pago.PROVEEDOR_SIMULADO);
        pago.setReferenciaProveedor("SIM-" + UUID.randomUUID());
        pago.setEstado(Pago.ESTADO_APROBADO);
        pago.setMonto(total);
        pago.setMoneda(orden.getMoneda());
        pago.setCreadoEn(ahora);
        pago.setPagadoEn(ahora);
        pagoRepository.save(pago);

        return aDto(orden, funcion, pago.getReferenciaProveedor());
    }

    @Transactional(readOnly = true)
    public List<OrdenDto> listar(String correo) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> ApiException.noEncontrado("Usuario no encontrado"));
        return ordenCompraRepository.findByUsuarioIdOrderByCreadoEnDesc(usuario.getId()).stream()
                .map(o -> {
                    FuncionEvento f = o.getDetalles().get(0).getEntradas().get(0).getAsiento().getFuncion();
                    String ref = pagoRepository.findFirstByOrdenIdOrderByIdDesc(o.getId())
                            .map(Pago::getReferenciaProveedor).orElse(null);
                    return aDto(o, f, ref);
                })
                .toList();
    }

    private OrdenDto aDto(OrdenCompra o, FuncionEvento f, String referencia) {
        List<EntradaDto> entradas = o.getDetalles().stream()
                .flatMap(d -> d.getEntradas().stream())
                .map(e -> new EntradaDto(e.getCodigoEntrada(), e.getAsiento().getSeccion(), e.getAsiento().getFila(),
                        e.getAsiento().getNumero(), e.getAsiento().getTipoEntrada().getNombre(), e.getEstado()))
                .toList();
        return new OrdenDto(o.getId(), o.getNumeroOrden(), o.getEstado(), f.getEvento().getNombre(),
                f.getFechaInicio(), f.getRecinto().getNombre(), o.getMontoTotal(), o.getMoneda(), o.getPagadoEn(),
                referencia, entradas);
    }

    private static String etiqueta(AsientoFuncion a) {
        return a.getSeccion() + " " + a.getFila() + a.getNumero();
    }

    private static String hex(int n) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, n).toUpperCase();
    }
}