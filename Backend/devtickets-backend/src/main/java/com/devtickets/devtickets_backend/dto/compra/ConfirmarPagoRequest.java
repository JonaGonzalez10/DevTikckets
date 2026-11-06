package com.devtickets.devtickets_backend.dto.compra;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ConfirmarPagoRequest(@NotNull @Valid DatosPagoRequest pago) {}
