package com.devtickets.devtickets_backend.service;

import com.devtickets.devtickets_backend.dto.catalogo.*;
import com.devtickets.devtickets_backend.exception.ApiException;
import com.devtickets.devtickets_backend.model.*;
import com.devtickets.devtickets_backend.repository.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoService {

    private final CategoriaRepository categoriaRepository;
    private final EventoRepository eventoRepository;
    private final ImagenEventoRepository imagenEventoRepository;
    private final FuncionEventoRepository funcionEventoRepository;
    private final TipoEntradaRepository tipoEntradaRepository;
    private final AsientoFuncionRepository asientoFuncionRepository;

    public List<CategoriaDto> categorias() {
        List<CategoriaDto> resultado = categoriaRepository.findAllByOrderByNombreAsc().stream()
                .map(c -> new CategoriaDto(c.getId(), c.getNombre()))
                .toList();
        log.debug("Categorias consultadas");
        return resultado;
    }

    public List<EventoResumenDto> eventos(Long categoriaId, String q) {
        String texto = q == null ? "" : q.trim();
        List<Evento> eventos = eventoRepository.buscarPublicados(categoriaId, texto);
        log.debug("Eventos publicados consultados");
        if (eventos.isEmpty()) {
            return List.of();
        }
        List<Long> ids = eventos.stream().map(Evento::getId).toList();

        Map<Long, String> imagenes = new HashMap<>();
        imagenEventoRepository.findByEventoIdInAndEsPrincipalTrue(ids)
                .forEach(i -> imagenes.putIfAbsent(i.getEvento().getId(), i.getUrlImagen()));

        Map<Long, FuncionEvento> proxima = new HashMap<>();
        funcionEventoRepository.proximasPorEventos(ids, ahora())
                .forEach(f -> proxima.putIfAbsent(f.getEvento().getId(), f));

        Map<Long, List<TipoEntrada>> tiposPorFuncion = proxima.isEmpty() ? Map.of()
                : tipoEntradaRepository
                        .findByFuncionIdInOrderByPrecioDesc(
                                proxima.values().stream().map(FuncionEvento::getId).toList())
                        .stream()
                        .filter(t -> TipoEntrada.ESTADO_ACTIVO.equals(t.getEstado()))
                        .collect(Collectors.groupingBy(t -> t.getFuncion().getId()));

        return eventos.stream()
                .filter(e -> proxima.containsKey(e.getId()))
                .map(e -> {
                    FuncionEvento f = proxima.get(e.getId());
                    List<TipoEntrada> tipos = tiposPorFuncion.getOrDefault(f.getId(), List.of());
                    // La lista llega ordenada por precio descendente
                    var max = tipos.isEmpty() ? null : tipos.get(0);
                    var min = tipos.isEmpty() ? null : tipos.get(tipos.size() - 1);
                    return new EventoResumenDto(
                            e.getId(),
                            e.getNombre(),
                            e.getCategoria() == null ? null : e.getCategoria().getNombre(),
                            imagenes.get(e.getId()),
                            f.getFechaInicio(),
                            f.getRecinto().getNombre(),
                            f.getRecinto().getCiudad(),
                            segmento(e.getDescripcion(), 0),
                            segmento(e.getDescripcion(), 1),
                            min == null ? null : min.getPrecio(),
                            max == null ? null : max.getPrecio(),
                            max == null ? null : max.getMoneda());
                })
                .toList();
    }

    // La descripcion demo tiene el formato "Artista | Genero | Recinto, Ciudad"
    private static String segmento(String descripcion, int indice) {
        if (descripcion == null) {
            return null;
        }
        String[] partes = descripcion.split("\\|");
        return partes.length > indice && partes.length > 1 ? partes[indice].trim() : null;
    }

    public EventoDetalleDto evento(Long id) {
        Evento e = eventoRepository.findPublicadoById(id)
                .orElseThrow(() -> {
                    log.warn("Consulta de evento rechazada: no encontrado o no publicado");
                    return ApiException.noEncontrado("Evento no encontrado");
                });
        log.debug("Detalle de evento consultado");
        LocalDateTime ahora = ahora();

        List<FuncionEvento> funciones = funcionEventoRepository.proximasPorEventos(List.of(id), ahora);
        Map<Long, List<TipoEntradaDto>> tipos = new HashMap<>();
        if (!funciones.isEmpty()) {
            tipoEntradaRepository
                    .findByFuncionIdInOrderByPrecioDesc(funciones.stream().map(FuncionEvento::getId).toList())
                    .stream()
                    .filter(t -> TipoEntrada.ESTADO_ACTIVO.equals(t.getEstado()))
                    .forEach(t -> tipos.computeIfAbsent(t.getFuncion().getId(), k -> new ArrayList<>())
                            .add(new TipoEntradaDto(t.getId(), t.getNombre(), t.getDescripcion(), t.getPrecio(),
                                    t.getMoneda(), t.disponibles())));
        }

        List<FuncionDto> funcionesDto = funciones.stream()
                .map(f -> new FuncionDto(f.getId(), f.getFechaInicio(), f.getFechaFin(), f.ventaAbierta(ahora),
                        f.getRecinto().getNombre(), direccion(f.getRecinto()), f.getRecinto().getCiudad(),
                        tipos.getOrDefault(f.getId(), List.of())))
                .toList();

        List<ImagenDto> imagenes = e.getImagenes().stream()
                .map(i -> new ImagenDto(i.getUrlImagen(), i.getTextoAlternativo(), i.isEsPrincipal()))
                .toList();

        return new EventoDetalleDto(e.getId(), e.getNombre(), e.getDescripcion(),
                e.getCategoria() == null ? null : e.getCategoria().getNombre(), imagenes, funcionesDto);
    }

    public List<AsientoDto> asientos(Long idFuncion) {
        LocalDateTime ahora = ahora();
        FuncionEvento f = funcionEventoRepository.findById(idFuncion)
                .filter(x -> FuncionEvento.ESTADO_PROGRAMADO.equals(x.getEstado()))
                .orElseThrow(() -> {
                    log.warn("Consulta de asientos rechazada: funcion no encontrada o no programada");
                    return ApiException.noEncontrado("Funcion no encontrada");
                });
        log.debug("Mapa de asientos consultado");
        return asientoFuncionRepository.mapaDeFuncion(f.getId()).stream()
                .map(a -> new AsientoDto(a.getId(), a.getSeccion(), a.getFila(), a.getNumero(),
                        a.getTipoEntrada().getId(), a.getTipoEntrada().getNombre(), a.getTipoEntrada().getPrecio(),
                        a.getTipoEntrada().getMoneda(),
                        AsientoFuncion.ESTADO_DISPONIBLE.equals(a.getEstado())
                                || (AsientoFuncion.ESTADO_RESERVADO.equals(a.getEstado())
                                        && a.getReserva() != null
                                        && a.getReserva().getExpiraEn() != null
                                        && !a.getReserva().getExpiraEn().isAfter(ahora))))
                .toList();
    }

    private static String direccion(Recinto recinto) {
        String direccion = recinto.getDireccion();
        return direccion == null || direccion.isBlank() ? recinto.getNombre() : direccion;
    }

    private static LocalDateTime ahora() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}