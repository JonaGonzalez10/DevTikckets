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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return categoriaRepository.findAllByOrderByNombreAsc().stream()
                .map(c -> new CategoriaDto(c.getId(), c.getNombre()))
                .toList();
    }

    public List<EventoResumenDto> eventos(Long categoriaId, String q) {
        String texto = q == null ? "" : q.trim();
        List<Evento> eventos = eventoRepository.buscarPublicados(categoriaId, texto);
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

        return eventos.stream()
                .map(e -> {
                    FuncionEvento f = proxima.get(e.getId());
                    return new EventoResumenDto(
                            e.getId(),
                            e.getNombre(),
                            e.getCategoria() == null ? null : e.getCategoria().getNombre(),
                            imagenes.get(e.getId()),
                            f == null ? null : f.getFechaInicio(),
                            f == null ? null : f.getRecinto().getNombre(),
                            f == null ? null : f.getRecinto().getCiudad());
                })
                .toList();
    }

    public EventoDetalleDto evento(Long id) {
        Evento e = eventoRepository.findPublicadoById(id)
                .orElseThrow(() -> ApiException.noEncontrado("Evento no encontrado"));
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
                        f.getRecinto().getNombre(), f.getRecinto().getDireccion(), f.getRecinto().getCiudad(),
                        tipos.getOrDefault(f.getId(), List.of())))
                .toList();

        List<ImagenDto> imagenes = e.getImagenes().stream()
                .map(i -> new ImagenDto(i.getUrlImagen(), i.getTextoAlternativo(), i.isEsPrincipal()))
                .toList();

        return new EventoDetalleDto(e.getId(), e.getNombre(), e.getDescripcion(),
                e.getCategoria() == null ? null : e.getCategoria().getNombre(), imagenes, funcionesDto);
    }

    public List<AsientoDto> asientos(Long idFuncion) {
        FuncionEvento f = funcionEventoRepository.findById(idFuncion)
                .filter(x -> FuncionEvento.ESTADO_PROGRAMADO.equals(x.getEstado()))
                .orElseThrow(() -> ApiException.noEncontrado("Funcion no encontrada"));
        return asientoFuncionRepository.mapaDeFuncion(f.getId()).stream()
                .map(a -> new AsientoDto(a.getId(), a.getSeccion(), a.getFila(), a.getNumero(),
                        a.getTipoEntrada().getId(), a.getTipoEntrada().getNombre(), a.getTipoEntrada().getPrecio(),
                        a.getTipoEntrada().getMoneda(),
                        AsientoFuncion.ESTADO_DISPONIBLE.equals(a.getEstado())))
                .toList();
    }

    private static LocalDateTime ahora() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}