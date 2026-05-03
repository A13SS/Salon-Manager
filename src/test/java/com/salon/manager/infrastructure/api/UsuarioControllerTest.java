package com.salon.manager.infrastructure.api;

import com.salon.manager.application.dto.response.UsuarioResponse;
import com.salon.manager.application.service.UsuarioAppService;
import com.salon.manager.common.enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioAppService usuarioAppService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarUsuarios_Exito() {
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        usuario.setEmail("test@email.com");
        usuario.setNombre("Test User");
        usuario.setRol(Rol.CLIENTE);

        when(usuarioAppService.listarTodos()).thenReturn(List.of(usuario));

        ResponseEntity<List<UsuarioResponse>> result = usuarioController.listarUsuarios();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("test@email.com", result.getBody().get(0).getEmail());
        verify(usuarioAppService, times(1)).listarTodos();
    }

    @Test
    void testListarUsuarios_Vacio() {
        when(usuarioAppService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<UsuarioResponse>> result = usuarioController.listarUsuarios();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void testObtenerUsuario_Exito() {
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        usuario.setEmail("test@email.com");
        usuario.setNombre("Test User");
        usuario.setRol(Rol.CLIENTE);

        when(usuarioAppService.obtenerPorId(1L)).thenReturn(usuario);

        ResponseEntity<UsuarioResponse> result = usuarioController.obtenerUsuario(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().getId());
        assertEquals("test@email.com", result.getBody().getEmail());
        verify(usuarioAppService, times(1)).obtenerPorId(1L);
    }

    @Test
    void testObtenerUsuario_Error() {
        when(usuarioAppService.obtenerPorId(999L))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        assertThrows(RuntimeException.class, () -> usuarioController.obtenerUsuario(999L));
    }

    @Test
    void testBorrarFisico_Exito() {
        doNothing().when(usuarioAppService).borrarFisico(1L);

        ResponseEntity<?> result = usuarioController.borrarFisico(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Usuario eliminado permanentemente (RGPD)", result.getBody());
        verify(usuarioAppService, times(1)).borrarFisico(1L);
    }

    @Test
    void testBorrarFisico_Error() {
        doThrow(new RuntimeException("Error al eliminar usuario"))
                .when(usuarioAppService).borrarFisico(1L);

        ResponseEntity<?> result = usuarioController.borrarFisico(1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Error al eliminar usuario", result.getBody());
    }

    @Test
    void testBorrarLogico_Exito() {
        doNothing().when(usuarioAppService).borrarLogico(1L);

        ResponseEntity<?> result = usuarioController.borrarLogico(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Usuario desactivado (borrado lógico)", result.getBody());
        verify(usuarioAppService, times(1)).borrarLogico(1L);
    }

    @Test
    void testBorrarLogico_Error() {
        doThrow(new RuntimeException("Usuario ya está desactivado"))
                .when(usuarioAppService).borrarLogico(1L);

        ResponseEntity<?> result = usuarioController.borrarLogico(1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Usuario ya está desactivado", result.getBody());
    }

    @Test
    void testCambiarRol_Exito() {
        doNothing().when(usuarioAppService).cambiarRol(1L, Rol.PROFESIONAL);

        ResponseEntity<?> result = usuarioController.cambiarRol(1L, Rol.PROFESIONAL);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Rol actualizado", result.getBody());
        verify(usuarioAppService, times(1)).cambiarRol(1L, Rol.PROFESIONAL);
    }

    @Test
    void testCambiarRol_Error() {
        doThrow(new RuntimeException("Usuario no encontrado"))
                .when(usuarioAppService).cambiarRol(1L, Rol.ADMIN);

        ResponseEntity<?> result = usuarioController.cambiarRol(1L, Rol.ADMIN);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Usuario no encontrado", result.getBody());
    }

    @Test
    void testCambiarRol_RolCliente() {
        doNothing().when(usuarioAppService).cambiarRol(1L, Rol.CLIENTE);

        ResponseEntity<?> result = usuarioController.cambiarRol(1L, Rol.CLIENTE);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Rol actualizado", result.getBody());
        verify(usuarioAppService, times(1)).cambiarRol(1L, Rol.CLIENTE);
    }

    @Test
    void testCambiarRol_RolAdmin() {
        doNothing().when(usuarioAppService).cambiarRol(1L, Rol.ADMIN);

        ResponseEntity<?> result = usuarioController.cambiarRol(1L, Rol.ADMIN);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Rol actualizado", result.getBody());
        verify(usuarioAppService, times(1)).cambiarRol(1L, Rol.ADMIN);
    }
}