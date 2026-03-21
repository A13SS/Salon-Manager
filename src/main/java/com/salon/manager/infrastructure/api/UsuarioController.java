package com.salon.manager.infrastructure.api;

import com.salon.manager.application.dto.response.UsuarioResponse;
import com.salon.manager.application.service.UsuarioAppService;
import com.salon.manager.common.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
        import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioAppService usuarioAppService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioAppService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESIONAL')")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(@PathVariable Long id) {

        UsuarioResponse usuario = usuarioAppService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> borrarFisico(@PathVariable Long id) {
        try {
            usuarioAppService.borrarFisico(id);
            return ResponseEntity.ok("Usuario eliminado permanentemente (RGPD)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> borrarLogico(@PathVariable Long id) {
        try {
            usuarioAppService.borrarLogico(id);
            return ResponseEntity.ok("Usuario desactivado (borrado lógico)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id,
                                        @RequestParam Rol nuevoRol) {
        try {
            usuarioAppService.cambiarRol(id, nuevoRol);
            return ResponseEntity.ok("Rol actualizado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
