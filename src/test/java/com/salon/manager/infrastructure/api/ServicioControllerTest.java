package com.salon.manager.infrastructure.api;

import com.salon.manager.application.dto.response.ServicioResponse;
import com.salon.manager.application.service.ServicioAppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioControllerTest {

    @Mock
    private ServicioAppService servicioAppService;

    @InjectMocks
    private ServicioController servicioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarActivos_Exito() {
        ServicioResponse servicio = new ServicioResponse();
        servicio.setId(1L);
        servicio.setNombre("Corte de pelo");
        servicio.setPrecio(new BigDecimal("25.00"));
        servicio.setDuracionMin(30);
        servicio.setActivo(true);

        when(servicioAppService.listarActivos()).thenReturn(List.of(servicio));

        ResponseEntity<?> result = servicioController.listarActivos();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, ((List<?>) result.getBody()).size());
        verify(servicioAppService, times(1)).listarActivos();
    }

    @Test
    void testListarActivos_Vacio() {
        when(servicioAppService.listarActivos()).thenReturn(List.of());

        ResponseEntity<?> result = servicioController.listarActivos();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(((List<?>) result.getBody()).isEmpty());
    }

    @Test
    void testListarServicios_Exito() {
        ServicioResponse servicio1 = new ServicioResponse();
        servicio1.setId(1L);
        servicio1.setNombre("Corte de pelo");
        servicio1.setActivo(true);

        ServicioResponse servicio2 = new ServicioResponse();
        servicio2.setId(2L);
        servicio2.setNombre("Tinte completo");
        servicio2.setActivo(false);

        when(servicioAppService.listarTodos()).thenReturn(List.of(servicio1, servicio2));

        ResponseEntity<?> result = servicioController.listarServicios();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, ((List<?>) result.getBody()).size());
        verify(servicioAppService, times(1)).listarTodos();
    }

    @Test
    void testListarServicios_Vacio() {
        when(servicioAppService.listarTodos()).thenReturn(List.of());

        ResponseEntity<?> result = servicioController.listarServicios();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(((List<?>) result.getBody()).isEmpty());
    }

    @Test
    void testListarServicios_Error() {
        when(servicioAppService.listarTodos())
                .thenThrow(new RuntimeException("Error de base de datos"));

        assertThrows(RuntimeException.class, () -> servicioController.listarServicios());
    }

    @Test
    void testListarActivos_Error() {
        when(servicioAppService.listarActivos())
                .thenThrow(new RuntimeException("Error al cargar servicios"));

        assertThrows(RuntimeException.class, () -> servicioController.listarActivos());
    }
}