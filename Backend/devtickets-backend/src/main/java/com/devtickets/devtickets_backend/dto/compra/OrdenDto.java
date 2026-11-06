package com.devtickets.devtickets_backend.dto.compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenDto(
        Long id,
        String numeroOrden,
        String estado,
        Long idFuncion,
        String evento,
        LocalDateTime fechaFuncion,
        String recinto,
        BigDecimal montoTotal,
        String moneda,
        LocalDateTime pagadoEn,
        String referenciaPago,
        List<EntradaDto> entradas,
        LocalDateTime expiraEn) {}