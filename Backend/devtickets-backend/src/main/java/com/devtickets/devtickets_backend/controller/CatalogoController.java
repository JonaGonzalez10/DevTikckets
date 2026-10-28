package com.devtickets.devtickets_backend.controller;

import com.devtickets.devtickets_backend.dto.catalogo.*;
import com.devtickets.devtickets_backend.service.CatalogoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping("/categorias")
    public List<CategoriaDto> categorias() {
        return catalogoService.categorias();
    }

    @GetMapping("/eventos")
    public List<EventoResumenDto> eventos(
            @RequestParam(required = false) Long categoriaId, @RequestParam(required = false) String q) {
        return catalogoService.eventos(categoriaId, q);
    }

    @GetMapping("/eventos/{id}")
    public EventoDetalleDto evento(@PathVariable Long id) {
        return catalogoService.evento(id);
    }

    @GetMapping("/funciones/{id}/asientos")
    public List<AsientoDto> asientos(@PathVariable Long id) {
        return catalogoService.asientos(id);
    }
}