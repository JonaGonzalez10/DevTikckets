package com.devtickets.devtickets_backend.dto.compra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos de tarjeta de una simulacion: solo se validan formato y longitud, nunca se almacenan. */
public record DatosPagoRequest(
        @NotBlank @Pattern(regexp = "^[0-9]{13,19}$", message = "La tarjeta debe tener entre 13 y 19 digitos") String numeroTarjeta,
        @NotBlank @Size(min = 3, max = 100, message = "El titular debe tener entre 3 y 100 caracteres") String titular,
        @NotBlank @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "La expiracion debe tener formato MM/AA") String expiracion,
        @NotBlank @Pattern(regexp = "^\\d{3,4}$", message = "El CVV debe tener 3 o 4 digitos") String cvv) {}