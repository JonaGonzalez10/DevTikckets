package com.devtickets.devtickets_backend.dto.catalogo;

import java.time.LocalDateTime;

public record EventoResumenDto(
        Long id,
        String nombre,
        String categoria,
        String imagenPrincipal,
        LocalDateTime proximaFecha,
        String recinto,
        String ciudad) {}