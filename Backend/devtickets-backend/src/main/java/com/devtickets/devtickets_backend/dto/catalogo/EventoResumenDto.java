package com.devtickets.devtickets_backend.dto.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoResumenDto(
        Long id,
        String nombre,
        String categoria,
        String imagenPrincipal,
        LocalDateTime proximaFecha,
        String recinto,
        String ciudad,
        String artista,
        String genero,
        BigDecimal precioMin,
        BigDecimal precioMax,
        String moneda) {}