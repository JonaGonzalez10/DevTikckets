package com.devtickets.devtickets_backend.dto.auth;

import java.util.List;

public record LoginResponse(
        String token,
        long expiraEnSegundos,
        Long idUsuario,
        String correoElectronico,
        String nombre,
        String apellido,
        List<String> roles) {
}
