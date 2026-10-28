package com.devtickets.devtickets_backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank @Email @Size(max = 254) String correoElectronico,
        @NotBlank @Size(min = 8, max = 72) String contrasena,
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @Pattern(regexp = "^[0-9+() -]{7,30}$") String telefono) {
}
