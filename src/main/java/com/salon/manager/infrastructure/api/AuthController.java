package com.salon.manager.infrastructure.api;

import com.salon.manager.application.dto.request.LoginRequest;
import com.salon.manager.application.dto.request.RegistroRequest;
import com.salon.manager.application.dto.response.LoginResponse;
import com.salon.manager.application.service.AuthService;
import com.salon.manager.application.service.UsuarioAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioAppService usuarioAppService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest dto) {
        try {
            usuarioAppService.registrar(dto.getEmail(), dto.getPassword(),
                    dto.getNombre(), dto.getTelefono());
            return ResponseEntity.ok("Usuario registrado correctamente!! Muchas gracias y le esperamos pronto.... Rol: CLIENTE");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Credenciales inválidas, por favor inténtelo de nuevo...");
        }
    }
}