package com.salon.manager.infrastructure.api;

import com.salon.manager.application.dto.request.CrearCitaRequest;
import com.salon.manager.application.dto.response.CitaResponse;
import com.salon.manager.application.dto.response.HuecoResponse;
import com.salon.manager.application.service.CitaAppService;
import com.salon.manager.common.enums.EstadoCita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CitaControllerTest {

    @Mock
    private CitaAppService citaAppService;

    @InjectMocks
    private CitaController citaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearCita_Exito() {
        CrearCitaRequest request = new CrearCitaRequest();
        request.setClienteId(100L);
        request.setProfesionalId(200L);
        request.setServicioId(300L);
        request.setFechaInicio(LocalDateTime.now().plusDays(1));

        CitaResponse response = new CitaResponse();
        response.setId(1L);
        response.setEstado(EstadoCita.PENDIENTE);

        when(citaAppService.crearCita(any(), any(), any(), any(), any(), any()))
                .thenReturn(response);

        ResponseEntity<?> result = citaController.crearCita(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, ((CitaResponse) result.getBody()).getId());
        verify(citaAppService, times(1)).crearCita(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testCrearCita_Error() {
        CrearCitaRequest request = new CrearCitaRequest();
        request.setClienteId(100L);
        request.setProfesionalId(200L);
        request.setServicioId(300L);
        request.setFechaInicio(LocalDateTime.now().plusDays(1));

        when(citaAppService.crearCita(any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Horario ocupado"));

        ResponseEntity<?> result = citaController.crearCita(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Horario ocupado", result.getBody());
    }

    @Test
    void testGetCitasCliente_Exito() {
        CitaResponse cita = new CitaResponse();
        cita.setId(1L);

        when(citaAppService.obtenerPorCliente(100L))
                .thenReturn(List.of(cita));

        ResponseEntity<List<CitaResponse>> result = citaController.getCitasCliente(100L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(citaAppService, times(1)).obtenerPorCliente(100L);
    }

    @Test
    void testGetCitasProfesional_Exito() {
        CitaResponse cita = new CitaResponse();
        cita.setId(2L);

        when(citaAppService.obtenerPorProfesional(200L))
                .thenReturn(List.of(cita));

        ResponseEntity<List<CitaResponse>> result = citaController.getCitasProfesional(200L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(citaAppService, times(1)).obtenerPorProfesional(200L);
    }

    @Test
    void testGetCitasPorFecha_Exito() {
        LocalDateTime fecha = LocalDateTime.now();

        when(citaAppService.obtenerPorFecha(fecha))
                .thenReturn(List.of());

        ResponseEntity<List<CitaResponse>> result = citaController.getCitasPorFecha(fecha);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void testGetHuecosDisponibles_Exito() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        HuecoResponse hueco = new HuecoResponse("10:00", true, "LIBRE");

        when(citaAppService.calcularHuecosDisponibles(fecha, 200L, 300L))
                .thenReturn(List.of(hueco));

        ResponseEntity<List<HuecoResponse>> result = citaController.getHuecosDisponibles(fecha, 200L, 300L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("10:00", result.getBody().get(0).getHora());
    }

    @Test
    void testMarcarAtendida_Exito() {
        CitaResponse response = new CitaResponse();
        response.setId(1L);
        response.setEstado(EstadoCita.REALIZADA);

        when(citaAppService.marcarAtendida(1L))
                .thenReturn(response);

        ResponseEntity<?> result = citaController.marcarAtendida(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(EstadoCita.REALIZADA, ((CitaResponse) result.getBody()).getEstado());
    }

    @Test
    void testMarcarAtendida_Error() {
        when(citaAppService.marcarAtendida(999L))
                .thenThrow(new RuntimeException("Cita no encontrada"));

        ResponseEntity<?> result = citaController.marcarAtendida(999L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Cita no encontrada", result.getBody());
    }

    @Test
    void testCancelarCita_Exito() {
        when(citaAppService.cancelarCita(1L)).thenReturn(new CitaResponse());

        ResponseEntity<?> result = citaController.cancelarCita(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Cita cencelada", ((Map<?, ?>) result.getBody()).get("mensaje"));
    }

    @Test
    void testCancelarCita_Error() {
        doThrow(new RuntimeException("No se puede cancelar"))
                .when(citaAppService).cancelarCita(1L);

        ResponseEntity<?> result = citaController.cancelarCita(1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("No se puede cancelar", ((Map<?, ?>) result.getBody()).get("error"));
    }

    @Test
    void testConfirmarCita_Exito() {
        when(citaAppService.confirmarCita(1L)).thenReturn(new CitaResponse());

        ResponseEntity<?> result = citaController.confirmarCita(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Cita confirmada", ((Map<?, ?>) result.getBody()).get("mensaje"));
    }

    @Test
    void testConfirmarCita_Error() {
        doThrow(new RuntimeException("Fecha pasada"))
                .when(citaAppService).confirmarCita(1L);

        ResponseEntity<?> result = citaController.confirmarCita(1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Fecha pasada", ((Map<?, ?>) result.getBody()).get("error"));
    }

    @Test
    void testEliminarCita_Exito() {
        doNothing().when(citaAppService).eliminarCita(1L);

        ResponseEntity<?> result = citaController.eliminarCita(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Cita eliminada", ((Map<?, ?>) result.getBody()).get("mensaje"));
    }

    @Test
    void testEliminarCita_Error() {
        doThrow(new RuntimeException("Error al eliminar"))
                .when(citaAppService).eliminarCita(1L);

        ResponseEntity<?> result = citaController.eliminarCita(1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Error al eliminar", ((Map<?, ?>) result.getBody()).get("error"));
    }
}