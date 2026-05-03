package com.salon.manager.application.service;

import com.salon.manager.application.dto.response.UsuarioResponse;
import com.salon.manager.common.enums.Rol;
import com.salon.manager.common.exception.ResourceNotFoundException;
import com.salon.manager.domain.model.Usuario;
import com.salon.manager.domain.repository.UsuarioRepository;
import com.salon.manager.logger.LoggerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioAppServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GestorRGPD gestorRGPD;

    @Mock
    private LoggerServiceImpl loggerService;

    @InjectMocks
    private UsuarioAppService usuarioAppService;

    private Usuario usuarioTest;

    @BeforeEach
    void setUp() {
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setEmail("test@email.com");
        usuarioTest.setPassword("encoded_password");
        usuarioTest.setNombre("Test User");
        usuarioTest.setTelefono("600123456");
        usuarioTest.setRol(Rol.CLIENTE);
        usuarioTest.setActivo(true);
        usuarioTest.setFechaRegistro(LocalDateTime.now());
    }

    @Test
    void testRegistrar_Exito() {
        String email = "nuevo@email.com";
        String password = "password123";
        String encodedPassword = "encoded_password";

        when(usuarioRepository.existeEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        doAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        }).when(usuarioRepository).guardar(any(Usuario.class));

        UsuarioResponse response = usuarioAppService.registrar(email, password, "Nuevo User", "611222333");

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getRol()).isEqualTo(Rol.CLIENTE);

        verify(usuarioRepository, times(1)).existeEmail(email);
        verify(passwordEncoder, times(1)).encode(password);
        verify(usuarioRepository, times(1)).guardar(any(Usuario.class));
        verify(loggerService, times(1)).logAccion(eq("REGISTRO_USUARIO"), anyString(), eq(2L));
    }

    @Test
    void testRegistrar_EmailExistente_LanzaExcepcion() {
        String email = "existente@email.com";
        when(usuarioRepository.existeEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> usuarioAppService.registrar(email, "pass", "Name", "600000000"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El email ya está registrado: " + email);

        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).guardar(any());

        //Esperamos 2 llamadas, la del if y la del catch
        verify(loggerService, times(2)).logError(anyString(), isNull());

        //Verificamos los mensajes específicos
        verify(loggerService).logError(eq("El email ya está registrado: " + email), isNull());
        verify(loggerService).logError(eq("Error al registrar usuario: El email ya está registrado: " + email), isNull());
    }

    @Test
    void testListarTodos_Exito() {
        when(usuarioRepository.listarTodos()).thenReturn(List.of(usuarioTest));

        List<UsuarioResponse> responses = usuarioAppService.listarTodos();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getEmail()).isEqualTo("test@email.com");
        verify(usuarioRepository, times(1)).listarTodos();
    }

    @Test
    void testListarTodos_Vacio() {
        when(usuarioRepository.listarTodos()).thenReturn(List.of());

        List<UsuarioResponse> responses = usuarioAppService.listarTodos();

        assertThat(responses).isEmpty();
    }

    @Test
    void testObtenerPorId_Exito() {
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(usuarioTest));

        UsuarioResponse response = usuarioAppService.obtenerPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@email.com");
        verify(usuarioRepository, times(1)).buscarPorId(1L);
    }

    @Test
    void testObtenerPorId_NoEncontrado_LanzaExcepcion() {
        when(usuarioRepository.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioAppService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado con ID: 999");

        verify(loggerService, times(1)).logError(anyString(), eq(999L));
    }

    @Test
    void testBorrarLogico_DelegaEnGestorRGPD() {
        doNothing().when(gestorRGPD).borrarLogico(1L);

        usuarioAppService.borrarLogico(1L);

        verify(gestorRGPD, times(1)).borrarLogico(1L);
        verify(loggerService, times(1)).logAccion(eq("BORRADO_LOGICO_USUARIO"), anyString(), eq(1L));
    }

    @Test
    void testBorrarFisico_DelegaEnGestorRGPD() {
        doNothing().when(gestorRGPD).borrarFisico(1L);

        usuarioAppService.borrarFisico(1L);

        verify(gestorRGPD, times(1)).borrarFisico(1L);
        verify(loggerService, times(1)).logAccion(eq("BORRADO_FISICO_USUARIO"), anyString(), eq(1L));
    }

    @Test
    void testCambiarRol_Exito() {
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(usuarioTest));
        doAnswer(invocation -> invocation.getArgument(0))
                .when(usuarioRepository).guardar(any(Usuario.class));

        usuarioAppService.cambiarRol(1L, Rol.PROFESIONAL);

        assertThat(usuarioTest.getRol()).isEqualTo(Rol.PROFESIONAL);
        verify(usuarioRepository, times(1)).buscarPorId(1L);
        verify(usuarioRepository, times(1)).guardar(usuarioTest);
        verify(loggerService, times(1)).logAccion(eq("CAMBIO_ROL"), anyString(), eq(1L));
    }

    @Test
    void testCambiarRol_UsuarioNoEncontrado_LanzaExcepcion() {
        when(usuarioRepository.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioAppService.cambiarRol(999L, Rol.ADMIN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(loggerService, times(1)).logError(anyString(), eq(999L));
    }

    @Test
    void testRegistrar_ErrorInesperado_RegistraLog() {
        String email = "error@email.com";
        when(usuarioRepository.existeEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("Error de encoding"));

        assertThatThrownBy(() -> usuarioAppService.registrar(email, "pass", "Name", "600000000"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error de encoding");

        verify(loggerService, times(1)).logError(eq("Error al registrar usuario: Error de encoding"), isNull());
    }

    @Test
    void testObtenerPorId_ExcepcionInesperada_SePropaga() {
        when(usuarioRepository.buscarPorId(1L)).thenThrow(new IllegalStateException("Error de BD"));

        assertThatThrownBy(() -> usuarioAppService.obtenerPorId(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Error de BD");

        verify(loggerService, times(1)).logError(anyString(), eq(1L));
    }
}