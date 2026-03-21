package com.salon.manager.infrastructure.api;

import com.salon.manager.application.dto.request.CrearCitaRequest;
import com.salon.manager.application.dto.response.CitaResponse;
import com.salon.manager.application.service.CitaAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaAppService citaAppService;

    @PostMapping("/crear")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> crearCita(@RequestBody @Valid CrearCitaRequest request) {
        try {
            CitaResponse cita = citaAppService.crearCita(
                    request.getClienteId(),
                    request.getProfesionalId(),
                    request.getServicioId(),
                    request.getFechaInicio(),
                    request.getAlergias(),
                    request.getObservaciones()
            );
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<CitaResponse>> getCitasCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(citaAppService.obtenerPorCliente(clienteId));
    }

    @GetMapping("/profesional/{profesionalId}")
    @PreAuthorize("hasRole('PROFESIONAL') or hasRole('ADMIN')")
    public ResponseEntity<List<CitaResponse>> getCitasProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(citaAppService.obtenerPorProfesional(profesionalId));
    }

    @PatchMapping("/{id}/atender")
    @PreAuthorize("hasRole('PROFESIONAL') or hasRole('ADMIN')")
    public ResponseEntity<?> marcarAtendida(@PathVariable Long id) {
        try {
            CitaResponse cita = citaAppService.marcarAtendida(id);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('PROFESIONAL') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelarCita(@PathVariable Long id) {
        try {
            CitaResponse cita = citaAppService.cancelarCita(id);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('PROFESIONAL') or hasRole('ADMIN')")
    public ResponseEntity<?> confirmarCita(@PathVariable Long id) {
        try {
            CitaResponse cita = citaAppService.confirmarCita(id);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESIONAL') or hasRole('ADMIN')")
    public ResponseEntity<?> eliminarCita(@PathVariable Long id) {
        try {
            citaAppService.eliminarCita(id);
            return ResponseEntity.ok("Cita eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
