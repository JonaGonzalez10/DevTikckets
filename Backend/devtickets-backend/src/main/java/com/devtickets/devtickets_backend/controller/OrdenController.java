package com.devtickets.devtickets_backend.controller;

import com.devtickets.devtickets_backend.dto.compra.*;
import com.devtickets.devtickets_backend.service.OrdenService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenDto crear(Authentication auth, @Valid @RequestBody CrearOrdenRequest request) {
        return ordenService.crear(auth.getName(), request);
    }

    @PostMapping("/{id}/pago")
    public OrdenDto confirmarPago(
            Authentication auth, @PathVariable Long id, @Valid @RequestBody ConfirmarPagoRequest request) {
        return ordenService.confirmarPago(auth.getName(), id, request);
    }

    @GetMapping
    public List<OrdenDto> listar(Authentication auth) {
        return ordenService.listar(auth.getName());
    }
}