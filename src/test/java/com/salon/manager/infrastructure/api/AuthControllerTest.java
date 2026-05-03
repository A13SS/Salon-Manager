package com.salon.manager.infrastructure.api;

import com.salon.manager.application.dto.request.LoginRequest;
import com.salon.manager.application.dto.request.RegistroRequest;
import com.salon.manager.application.dto.response.LoginResponse;
import com.salon.manager.application.service.AuthService;
import com.salon.manager.application.service.UsuarioAppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UsuarioAppService usuarioAppService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrar_Exito() {
        RegistroRequest request = new RegistroRequest();
        request.setEmail("test@email.com");
        request.setPassword("password123");
        request.setNombre("Test User");
        request.setTelefono("600123456");

        when(usuarioAppService.registrar(any(), any(), any(), any()))
                .thenReturn(null);

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("success"));
        verify(usuarioAppService, times(1)).registrar(any(), any(), any(), any());
    }

    @Test
    void testRegistrar_Error() {
        RegistroRequest request = new RegistroRequest();
        request.setEmail("test@email.com");
        request.setPassword("password123");

        doThrow(new RuntimeException("Email duplicado"))
                .when(usuarioAppService).registrar(any(), any(), any(), any());

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((Map<?, ?>) response.getBody()).get("success"));
    }

    @Test
    void testLogin_Exito() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@email.com");
        request.setPassword("password123");

        LoginResponse loginResponse = new LoginResponse(
                "jwt.token",
                "test@email.com",
                "CLIENTE",
                1L
        );

        when(authService.login("test@email.com", "password123")).thenReturn(loginResponse);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt.token", ((LoginResponse) response.getBody()).getToken());
        verify(authService, times(1)).login("test@email.com", "password123");
    }

    @Test
    void testLogin_Error() {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@email.com");
        request.setPassword("wrongpass");

        when(authService.login(any(), any()))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(false, ((Map<?, ?>) response.getBody()).get("success"));
    }
}