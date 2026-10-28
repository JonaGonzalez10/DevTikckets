package com.devtickets.devtickets_backend.dto.compra;

public record EntradaDto(
        String codigoEntrada, String seccion, String fila, int numero, String tipoEntrada, String estado) {}