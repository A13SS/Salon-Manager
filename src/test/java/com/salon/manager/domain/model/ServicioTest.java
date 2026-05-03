package com.salon.manager.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

class ServicioTest {

    private Servicio servicio;

    @BeforeEach
    void setUp() {
        servicio = new Servicio();
        servicio.setId(1L);
        servicio.setNombre("Corte de pelo");
        servicio.setDuracionMin(30);
        servicio.setPrecio(new BigDecimal("25.00"));
    }

    @Test
    void testCrearServicio_ActivoPorDefecto() {
        Servicio nuevoServicio = new Servicio();

        assertThat(nuevoServicio.getActivo()).isTrue();
    }

    @Test
    void testSetters_Getters() {
        Long id = 2L;
        String nombre = "Tinte completo";
        Integer duracion = 90;
        BigDecimal precio = new BigDecimal("85.50");
        Boolean activo = false;

        servicio.setId(id);
        servicio.setNombre(nombre);
        servicio.setDuracionMin(duracion);
        servicio.setPrecio(precio);
        servicio.setActivo(activo);

        assertThat(servicio.getId()).isEqualTo(id);
        assertThat(servicio.getNombre()).isEqualTo(nombre);
        assertThat(servicio.getDuracionMin()).isEqualTo(duracion);
        assertThat(servicio.getPrecio()).isEqualTo(precio);
        assertThat(servicio.getActivo()).isEqualTo(activo);
    }

    @Test
    void testPrecio_Cero() {
        servicio.setPrecio(BigDecimal.ZERO);

        assertThat(servicio.getPrecio()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void testDuracion_Cero() {
        servicio.setDuracionMin(0);

        assertThat(servicio.getDuracionMin()).isEqualTo(0);
    }

    @Test
    void testNombre_Vacio() {
        servicio.setNombre("");

        assertThat(servicio.getNombre()).isEmpty();
    }

    @Test
    void testCambiarEstadoActivo() {
        assertThat(servicio.getActivo()).isTrue();

        servicio.setActivo(false);

        assertThat(servicio.getActivo()).isFalse();

        servicio.setActivo(true);

        assertThat(servicio.getActivo()).isTrue();
    }
}