package com.salon.manager.application.service;

import com.salon.manager.common.enums.Rol;
import com.salon.manager.domain.model.Usuario;
import com.salon.manager.domain.repository.UsuarioRepository;
import com.salon.manager.logger.LoggerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestorRGPDTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LoggerServiceImpl loggerService;

    @InjectMocks
    private GestorRGPD gestorRGPD;

    private Usuario usuarioTest;

    @BeforeEach
    void setUp() {
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setEmail("test@email.com");
        usuarioTest.setNombre("Test User");
        usuarioTest.setRol(Rol.CLIENTE);
        usuarioTest.setActivo(true);
        usuarioTest.setTelefono("600123456");
    }

    @Test
    void testBorrarLogico_Exito() {
        Long usuarioId = 1L;

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.of(usuarioTest));

        //doAnswer para simular el guardado y retornar el mismo usuario
        doAnswer(invocation -> invocation.getArgument(0))
                .when(usuarioRepository).guardar(any(Usuario.class));

        gestorRGPD.borrarLogico(usuarioId);

        assertThat(usuarioTest.getActivo()).isFalse();

        verify(usuarioRepository, times(1)).buscarPorId(usuarioId);
        verify(usuarioRepository, times(1)).guardar(usuarioTest);
        verify(loggerService, never()).logError(anyString(), anyLong());
    }

    @Test
    void testBorrarLogico_UsuarioNoEncontrado_LanzaExcepcion() {
        Long usuarioId = 999L;

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestorRGPD.borrarLogico(usuarioId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(usuarioRepository, times(1)).buscarPorId(usuarioId);
        verify(usuarioRepository, never()).guardar(any());
        verify(loggerService, times(1))
                .logError(eq("Error en borrado lógico RGPD: Usuario no encontrado"), eq(usuarioId));
    }

    @Test
    void testBorrarLogico_ErrorAlGuardar_LanzaExcepcion() {
        Long usuarioId = 1L;
        String errorMsg = "Error de base de datos";

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.of(usuarioTest));

        doThrow(new RuntimeException(errorMsg))
                .when(usuarioRepository).guardar(any(Usuario.class));

        assertThatThrownBy(() -> gestorRGPD.borrarLogico(usuarioId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(errorMsg);

        verify(loggerService, times(1))
                .logError(eq("Error en borrado lógico RGPD: " + errorMsg), eq(usuarioId));
    }

    @Test
    void testBorrarLogico_YaDesactivado_LanzaExcepcion() {
        Long usuarioId = 1L;
        usuarioTest.setActivo(false);

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.of(usuarioTest));

        assertThatThrownBy(() -> gestorRGPD.borrarLogico(usuarioId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El usuario ya está desactivado");

        verify(usuarioRepository, never()).guardar(any());
        verify(loggerService, times(1))
                .logError(anyString(), eq(usuarioId));
    }

    @Test
    void testBorrarFisico_Exito() {
        Long usuarioId = 1L;

        doNothing().when(usuarioRepository).eliminar(usuarioId);

        gestorRGPD.borrarFisico(usuarioId);

        verify(usuarioRepository, times(1)).eliminar(usuarioId);
        verify(loggerService, never()).logError(anyString(), anyLong());
    }

    @Test
    void testBorrarFisico_Error_LanzaExcepcion() {
        Long usuarioId = 1L;
        String errorMsg = "Violación de clave foránea";

        doThrow(new RuntimeException(errorMsg))
                .when(usuarioRepository).eliminar(usuarioId);

        assertThatThrownBy(() -> gestorRGPD.borrarFisico(usuarioId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(errorMsg);

        verify(loggerService, times(1))
                .logError(eq("Error en borrado físico RGPD: " + errorMsg), eq(usuarioId));
    }

    @Test
    void testBorrarFisico_UsuarioNoExiste_NoLanzaExcepcion() {
        Long usuarioId = 999L;

        doNothing().when(usuarioRepository).eliminar(usuarioId);

        gestorRGPD.borrarFisico(usuarioId);

        verify(usuarioRepository, times(1)).eliminar(usuarioId);

        //No se registra error porque la eliminación fue "exitosa"
        verify(loggerService, never()).logError(anyString(), anyLong());
    }

    @Test
    void testBorrarLogico_UsuarioIdNull_LanzaExcepcion() {
        Long usuarioId = null;

        assertThatThrownBy(() -> gestorRGPD.borrarLogico(usuarioId))
                .isInstanceOf(RuntimeException.class);

        verify(loggerService, times(1))
                .logError(anyString(), isNull());
    }

    @Test
    void testBorrarLogico_ExcepcionInesperada_SePropaga() {
        Long usuarioId = 1L;

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenThrow(new IllegalStateException("Error inesperado"));

        assertThatThrownBy(() -> gestorRGPD.borrarLogico(usuarioId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Error inesperado");

        verify(loggerService, times(1))
                .logError(anyString(), eq(usuarioId));
    }
}