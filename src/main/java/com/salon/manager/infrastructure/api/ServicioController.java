package com.salon.manager.infrastructure.api;

import com.salon.manager.application.service.ServicioAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioAppService servicioAppService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESIONAL')")
    public ResponseEntity<?> listarServicios() {
        return ResponseEntity.ok(servicioAppService.listarActivos());
    }
}