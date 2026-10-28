package com.devtickets.devtickets_backend.dto.catalogo;

import java.math.BigDecimal;

public record TipoEntradaDto(
        Long id, String nombre, String descripcion, BigDecimal precio, String moneda, int disponibles) {}