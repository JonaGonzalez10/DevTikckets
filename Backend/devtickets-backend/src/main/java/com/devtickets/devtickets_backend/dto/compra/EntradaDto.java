package com.devtickets.devtickets_backend.dto.compra;

public record EntradaDto(
        Long idAsiento, String codigoEntrada, String seccion, String fila, int numero, String tipoEntrada, String estado) {}