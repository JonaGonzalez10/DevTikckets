package com.devtickets.devtickets_backend.dto.catalogo;

import java.math.BigDecimal;

public record AsientoDto(
        Long id,
        String seccion,
        String fila,
        int numero,
        Long idTipoEntrada,
        String tipoEntrada,
        BigDecimal precio,
        String moneda,
        boolean disponible) {}