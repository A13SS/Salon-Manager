package com.salon.manager.application.service;

import com.salon.manager.application.dto.response.CitaResponse;
import com.salon.manager.application.dto.response.HuecoResponse;
import com.salon.manager.common.enums.EstadoCita;
import com.salon.manager.common.enums.Rol;
import com.salon.manager.common.exception.ResourceNotFoundException;
import com.salon.manager.domain.model.Cita;
import com.salon.manager.domain.model.Servicio;
import com.salon.manager.domain.model.Usuario;
import com.salon.manager.domain.repository.CitaRepository;
import com.salon.manager.domain.repository.ServicioRepository;
import com.salon.manager.domain.repository.UsuarioRepository;
import com.salon.manager.domain.service.ValidadorSolapes;
import com.salon.manager.logger.LoggerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaAppServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private ValidadorSolapes validadorSolapes;

    @Mock
    private LoggerServiceImpl loggerService;

    @InjectMocks
    private CitaAppService citaAppService;

    private Cita citaTest;
    private Usuario clienteTest;
    private Usuario profesionalTest;
    private Servicio servicioTest;

    @BeforeEach
    void setUp() {
        clienteTest = new Usuario();
        clienteTest.setId(100L);
        clienteTest.setNombre("Cliente Test");
        clienteTest.setRol(Rol.CLIENTE);

        profesionalTest = new Usuario();
        profesionalTest.setId(200L);
        profesionalTest.setNombre("Profesional Test");
        profesionalTest.setRol(Rol.PROFESIONAL);

        servicioTest = new Servicio();
        servicioTest.setId(300L);
        servicioTest.setNombre("Corte de pelo");
        servicioTest.setDuracionMin(30);
        servicioTest.setPrecio(new BigDecimal("25.00"));

        citaTest = new Cita();
        citaTest.setId(1L);
        citaTest.setClienteId(100L);
        citaTest.setProfesionalId(200L);
        citaTest.setServicioId(300L);
        citaTest.setFechaInicio(LocalDateTime.now().plusDays(1));
        citaTest.setFechaFin(citaTest.getFechaInicio().plusMinutes(30));
        citaTest.setEstado(EstadoCita.PENDIENTE);
        citaTest.setAtendido(false);
    }

    @Test
    void testCrearCita_Exito() {
        LocalDateTime fechaInicio = LocalDateTime.now().plusDays(1);

        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.of(clienteTest));
        when(usuarioRepository.buscarPorId(200L)).thenReturn(Optional.of(profesionalTest));
        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));
        doNothing().when(validadorSolapes).validar(eq(200L), any(), any());

        doAnswer(invocation -> invocation.getArgument(0))
                .when(citaRepository).guardar(any(Cita.class));

        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.of(clienteTest));
        when(usuarioRepository.buscarPorId(200L)).thenReturn(Optional.of(profesionalTest));
        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));

        CitaResponse response = citaAppService.crearCita(
                100L, 200L, 300L, fechaInicio, "Sin alergias", "Primera visita"
        );

        assertThat(response).isNotNull();
        assertThat(response.getClienteNombre()).isEqualTo("Cliente Test");

        verify(usuarioRepository, atLeastOnce()).buscarPorId(100L);
        verify(usuarioRepository, atLeastOnce()).buscarPorId(200L);
        verify(servicioRepository, atLeastOnce()).buscarPorId(300L);
        verify(validadorSolapes, times(1)).validar(eq(200L), any(), any());
        verify(citaRepository, times(1)).guardar(any(Cita.class));
        verify(loggerService, times(2)).logAccion(anyString(), anyString(), eq(100L));
    }

    @Test
    void testCrearCita_ClienteNoEncontrado_LanzaExcepcion() {
        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                citaAppService.crearCita(100L, 200L, 300L, LocalDateTime.now().plusDays(1), null, null)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cliente no encontrado");

        verify(usuarioRepository, times(1)).buscarPorId(100L);
        verify(usuarioRepository, never()).buscarPorId(200L);
        verify(citaRepository, never()).guardar(any());
    }

    @Test
    void testCrearCita_SolapeHorario_LanzaExcepcion() {
        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.of(clienteTest));
        when(usuarioRepository.buscarPorId(200L)).thenReturn(Optional.of(profesionalTest));
        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));
        doThrow(new RuntimeException("Horario ocupado"))
                .when(validadorSolapes).validar(anyLong(), any(), any());

        assertThatThrownBy(() ->
                citaAppService.crearCita(100L, 200L, 300L, LocalDateTime.now().plusDays(1), null, null)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Horario ocupado");

        verify(validadorSolapes, times(1)).validar(eq(200L), any(), any());
        verify(citaRepository, never()).guardar(any());
    }

    @Test
    void testEliminarCita_Exito() {
        doNothing().when(citaRepository).eliminar(1L);

        citaAppService.eliminarCita(1L);

        verify(citaRepository, times(1)).eliminar(1L);
    }

    @Test
    void testMarcarAtendida_Exito() {
        when(citaRepository.buscarPorId(1L)).thenReturn(Optional.of(citaTest));

        doAnswer(invocation -> invocation.getArgument(0))
                .when(citaRepository).guardar(any(Cita.class));

        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.of(clienteTest));
        when(usuarioRepository.buscarPorId(200L)).thenReturn(Optional.of(profesionalTest));
        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));

        CitaResponse response = citaAppService.marcarAtendida(1L);

        assertThat(response.getEstado()).isEqualTo(EstadoCita.REALIZADA);
        verify(citaRepository, times(1)).buscarPorId(1L);
        verify(citaRepository, times(1)).guardar(any(Cita.class));
    }

    @Test
    void testMarcarAtendida_CitaNoEncontrada_LanzaExcepcion() {
        when(citaRepository.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> citaAppService.marcarAtendida(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cita no encontrada");
    }

    @Test
    void testConfirmarCita_FechaFutura_Exito() {
        Cita citaFutura = new Cita();
        citaFutura.setId(2L);
        citaFutura.setFechaInicio(LocalDateTime.now().plusHours(2));
        citaFutura.setEstado(EstadoCita.PENDIENTE);

        citaFutura.setClienteId(100L);
        citaFutura.setProfesionalId(200L);
        citaFutura.setServicioId(300L);

        when(citaRepository.buscarPorId(2L)).thenReturn(Optional.of(citaFutura));

        doAnswer(invocation -> invocation.getArgument(0))
                .when(citaRepository).guardar(any(Cita.class));

        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.of(clienteTest));
        when(usuarioRepository.buscarPorId(200L)).thenReturn(Optional.of(profesionalTest));
        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));

        CitaResponse response = citaAppService.confirmarCita(2L);

        assertThat(response.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    void testConfirmarCita_FechaPasada_LanzaExcepcion() {
        Cita citaPasada = new Cita();
        citaPasada.setId(3L);
        citaPasada.setFechaInicio(LocalDateTime.now().minusHours(1));

        when(citaRepository.buscarPorId(3L)).thenReturn(Optional.of(citaPasada));

        assertThatThrownBy(() -> citaAppService.confirmarCita(3L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede confirmar una cita con fecha pasada");
    }

    @Test
    void testCancelarCita_Pendiente_Exito() {
        Cita citaPendiente = new Cita();
        citaPendiente.setId(4L);
        citaPendiente.setEstado(EstadoCita.PENDIENTE);
        citaPendiente.setClienteId(100L);
        citaPendiente.setProfesionalId(200L);
        citaPendiente.setServicioId(300L);

        when(citaRepository.buscarPorId(4L)).thenReturn(Optional.of(citaPendiente));

        doAnswer(invocation -> invocation.getArgument(0))
                .when(citaRepository).guardar(any(Cita.class));

        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.of(clienteTest));
        when(usuarioRepository.buscarPorId(200L)).thenReturn(Optional.of(profesionalTest));
        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));

        CitaResponse response = citaAppService.cancelarCita(4L);

        assertThat(response.getEstado()).isEqualTo(EstadoCita.CANCELADA);
    }

    @Test
    void testCancelarCita_Realizada_LanzaExcepcion() {
        Cita citaRealizada = new Cita();
        citaRealizada.setId(5L);
        citaRealizada.setEstado(EstadoCita.REALIZADA);
        citaRealizada.setClienteId(100L);

        when(citaRepository.buscarPorId(5L)).thenReturn(Optional.of(citaRealizada));

        assertThatThrownBy(() -> citaAppService.cancelarCita(5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede cancelar una cita ya realizada");
    }

    @Test
    void testObtenerPorCliente_Exito() {
        when(citaRepository.buscarPorCliente(100L)).thenReturn(List.of(citaTest));

        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.of(clienteTest));
        when(usuarioRepository.buscarPorId(200L)).thenReturn(Optional.of(profesionalTest));
        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));

        List<CitaResponse> respuestas = citaAppService.obtenerPorCliente(100L);

        assertThat(respuestas).isNotEmpty();
        verify(citaRepository, times(1)).buscarPorCliente(100L);
    }

    @Test
    void testObtenerPorCliente_Vacio() {
        when(citaRepository.buscarPorCliente(100L)).thenReturn(Collections.emptyList());

        List<CitaResponse> respuestas = citaAppService.obtenerPorCliente(100L);

        assertThat(respuestas).isEmpty();
    }

    @Test
    void testObtenerPorProfesional_Exito() {
        when(citaRepository.buscarPorProfesional(200L)).thenReturn(List.of(citaTest));

        when(usuarioRepository.buscarPorId(100L)).thenReturn(Optional.of(clienteTest));
        when(usuarioRepository.buscarPorId(200L)).thenReturn(Optional.of(profesionalTest));
        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));

        List<CitaResponse> respuestas = citaAppService.obtenerPorProfesional(200L);

        assertThat(respuestas).hasSize(1);
        verify(citaRepository, times(1)).buscarPorProfesional(200L);
    }

    @Test
    void testCalcularHuecosDisponibles_Exito() {
        LocalDate fechaFutura = LocalDate.now().plusDays(1);

        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));
        when(citaRepository.buscarPorProfesionalYFecha(eq(200L), eq(fechaFutura)))
                .thenReturn(Collections.emptyList());

        List<HuecoResponse> huecos = citaAppService.calcularHuecosDisponibles(
                fechaFutura, 200L, 300L
        );

        assertThat(huecos).isNotEmpty();
        assertThat(huecos).allMatch(h -> h.getHora() != null);
    }

    @Test
    void testCalcularHuecos_FechaPasada_LanzaExcepcion() {
        LocalDate fechaPasada = LocalDate.now().minusDays(1);

        assertThatThrownBy(() ->
                citaAppService.calcularHuecosDisponibles(fechaPasada, 200L, 300L)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pueden seleccionar fechas pasadas");
    }

    @Test
    void testCalcularHuecos_ServicioNoEncontrado_LanzaExcepcion() {
        when(servicioRepository.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                citaAppService.calcularHuecosDisponibles(
                        LocalDate.now().plusDays(1), 200L, 999L
                )
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Servicio no encontrado");
    }

    @Test
    void testCalcularHuecos_ConCitasExistentes_FiltraOcupados() {
        LocalDate fecha = LocalDate.now().plusDays(1);

        Cita citaOcupada = new Cita();
        citaOcupada.setFechaInicio(LocalDateTime.of(fecha, java.time.LocalTime.of(10, 0)));
        citaOcupada.setServicioId(300L);

        when(servicioRepository.buscarPorId(300L)).thenReturn(Optional.of(servicioTest));
        when(citaRepository.buscarPorProfesionalYFecha(eq(200L), eq(fecha)))
                .thenReturn(List.of(citaOcupada));

        List<HuecoResponse> huecos = citaAppService.calcularHuecosDisponibles(
                fecha, 200L, 300L
        );

        assertThat(huecos).isNotEmpty();
        assertThat(huecos).anyMatch(h -> !h.isDisponible());
    }
}