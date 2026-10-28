package com.devtickets.devtickets_backend.dto.catalogo;

import java.time.LocalDateTime;
import java.util.List;

public record FuncionDto(
        Long id,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        boolean ventaAbierta,
        String recinto,
        String direccion,
        String ciudad,
        List<TipoEntradaDto> tiposEntrada) {}