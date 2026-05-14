package com.salon.manager.application.service;

import com.salon.manager.application.dto.response.ServicioResponse;
import com.salon.manager.domain.model.Servicio;
import com.salon.manager.domain.repository.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioAppServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ServicioAppService servicioAppService;

    private Servicio servicioActivo1;
    private Servicio servicioActivo2;
    private Servicio servicioInactivo;

    @BeforeEach
    void setUp() {
        servicioActivo1 = new Servicio();
        servicioActivo1.setId(1L);
        servicioActivo1.setNombre("Corte de pelo");
        servicioActivo1.setDuracionMin(30);
        servicioActivo1.setPrecio(new BigDecimal("25.00"));
        servicioActivo1.setActivo(true);

        servicioActivo2 = new Servicio();
        servicioActivo2.setId(2L);
        servicioActivo2.setNombre("Tinte completo");
        servicioActivo2.setDuracionMin(90);
        servicioActivo2.setPrecio(new BigDecimal("85.50"));
        servicioActivo2.setActivo(true);

        servicioInactivo = new Servicio();
        servicioInactivo.setId(3L);
        servicioInactivo.setNombre("Servicio antiguo");
        servicioInactivo.setDuracionMin(45);
        servicioInactivo.setPrecio(new BigDecimal("40.00"));
        servicioInactivo.setActivo(false);
    }

    @Test
    void testListarActivos_Exito() {
        when(servicioRepository.listarActivos()).thenReturn(List.of(servicioActivo1, servicioActivo2));

        List<ServicioResponse> responses = servicioAppService.listarActivos();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("id").containsExactlyInAnyOrder(1L, 2L);
        assertThat(responses).extracting("nombre").containsExactlyInAnyOrder("Corte de pelo", "Tinte completo");
        assertThat(responses).extracting("activo").containsOnly(true);

        verify(servicioRepository, times(1)).listarActivos();
        verify(servicioRepository, never()).listarTodos();
    }

    @Test
    void testListarActivos_Vacio() {
        when(servicioRepository.listarActivos()).thenReturn(List.of());

        List<ServicioResponse> responses = servicioAppService.listarActivos();

        assertThat(responses).isEmpty();
        verify(servicioRepository, times(1)).listarActivos();
    }

    @Test
    void testListarTodos_Exito() {
        when(servicioRepository.listarTodos()).thenReturn(List.of(servicioActivo1, servicioActivo2, servicioInactivo));

        List<ServicioResponse> responses = servicioAppService.listarTodos();

        assertThat(responses).hasSize(3);
        assertThat(responses).extracting("id").containsExactlyInAnyOrder(1L, 2L, 3L);
        assertThat(responses).extracting("nombre").containsExactlyInAnyOrder("Corte de pelo", "Tinte completo", "Servicio antiguo");
        assertThat(responses).extracting("activo").containsExactlyInAnyOrder(true, true, false);

        verify(servicioRepository, times(1)).listarTodos();
        verify(servicioRepository, never()).listarActivos();
    }

    @Test
    void testListarTodos_Vacio() {
        when(servicioRepository.listarTodos()).thenReturn(List.of());

        List<ServicioResponse> responses = servicioAppService.listarTodos();

        assertThat(responses).isEmpty();
        verify(servicioRepository, times(1)).listarTodos();
    }

    @Test
    void testListarActivos_MapeoCorrecto() {
        when(servicioRepository.listarActivos()).thenReturn(List.of(servicioActivo1));

        List<ServicioResponse> responses = servicioAppService.listarActivos();

        ServicioResponse response = responses.get(0);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Corte de pelo");
        assertThat(response.getDuracionMin()).isEqualTo(30);
        assertThat(response.getPrecio()).isEqualTo(new BigDecimal("25.00"));
        assertThat(response.getActivo()).isTrue();
    }

    @Test
    void testListarActivos_PreciosDecimales() {
        Servicio servicioConDecimales = new Servicio();
        servicioConDecimales.setId(4L);
        servicioConDecimales.setNombre("Tratamiento especial");
        servicioConDecimales.setDuracionMin(60);
        servicioConDecimales.setPrecio(new BigDecimal("123.45"));
        servicioConDecimales.setActivo(true);

        when(servicioRepository.listarActivos()).thenReturn(List.of(servicioConDecimales));

        List<ServicioResponse> responses = servicioAppService.listarActivos();

        assertThat(responses.get(0).getPrecio()).isEqualTo(new BigDecimal("123.45"));
    }

    @Test
    void testListarActivos_DuracionesExtremas() {
        Servicio servicioDuracionCero = new Servicio();
        servicioDuracionCero.setId(5L);
        servicioDuracionCero.setNombre("Consulta rápida");
        servicioDuracionCero.setDuracionMin(0);
        servicioDuracionCero.setPrecio(BigDecimal.ZERO);
        servicioDuracionCero.setActivo(true);

        when(servicioRepository.listarActivos()).thenReturn(List.of(servicioDuracionCero));

        List<ServicioResponse> responses = servicioAppService.listarActivos();

        assertThat(responses.get(0).getDuracionMin()).isEqualTo(0);
        assertThat(responses.get(0).getPrecio()).isEqualTo(BigDecimal.ZERO);
    }
}