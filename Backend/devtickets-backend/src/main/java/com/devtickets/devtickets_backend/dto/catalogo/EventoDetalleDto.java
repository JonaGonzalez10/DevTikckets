package com.devtickets.devtickets_backend.dto.catalogo;

import java.util.List;

public record EventoDetalleDto(
        Long id,
        String nombre,
        String descripcion,
        String categoria,
        List<ImagenDto> imagenes,
        List<FuncionDto> funciones) {}