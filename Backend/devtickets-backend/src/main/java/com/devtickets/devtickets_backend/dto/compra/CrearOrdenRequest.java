package com.devtickets.devtickets_backend.dto.compra;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CrearOrdenRequest(
        @NotNull Long idFuncion,
        @NotEmpty @Size(max = 8, message = "Maximo 8 asientos por orden") List<@NotNull Long> idsAsiento,
        @NotNull @Valid DatosPagoRequest pago) {}