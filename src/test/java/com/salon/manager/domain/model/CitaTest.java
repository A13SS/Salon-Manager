package com.salon.manager.domain.model;

import com.salon.manager.common.enums.EstadoCita;
import com.salon.manager.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class CitaTest {

    private Cita cita;

    @BeforeEach
    void setUp() {
        cita = new Cita();
        cita.setId(1L);
        cita.setClienteId(100L);
        cita.setProfesionalId(200L);
        cita.setServicioId(300L);
        cita.setFechaInicio(LocalDateTime.now().plusDays(1));
        cita.setFechaFin(LocalDateTime.now().plusDays(1).plusMinutes(30));
    }

    @Test
    void testCrearCita_EstadoPorDefecto() {
        Cita nuevaCita = new Cita();

        assertThat(nuevaCita.getEstado()).isEqualTo(EstadoCita.PENDIENTE);
        assertThat(nuevaCita.isAtendido()).isFalse();
        assertThat(nuevaCita.getFechaCreacion()).isNotNull();
    }

    @Test
    void testMarcarAtendida_Exito() {
        cita.setAtendido(false);

        cita.marcarAtendida();

        assertThat(cita.isAtendido()).isTrue();
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.REALIZADA);
    }

    @Test
    void testMarcarAtendida_YaAtendida_LanzaExcepcion() {
        cita.setAtendido(true);

        assertThatThrownBy(() -> cita.marcarAtendida())
                .isInstanceOf(DomainException.class)
                .hasMessage("La cita ya está atendida");
    }

    @Test
    void testCancelar_Exito() {
        cita.setEstado(EstadoCita.PENDIENTE);

        cita.cancelar();

        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CANCELADA);
    }

    @Test
    void testCancelar_CitaConfirmada_Exito() {
        cita.setEstado(EstadoCita.CONFIRMADA);

        cita.cancelar();

        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CANCELADA);
    }

    @Test
    void testCancelar_CitaRealizada_LanzaExcepcion() {
        cita.setEstado(EstadoCita.REALIZADA);

        assertThatThrownBy(() -> cita.cancelar())
                .isInstanceOf(DomainException.class)
                .hasMessage("No se puede cancelar una cita ya realizada");
    }

    @Test
    void testConfirmar_FechaFutura_Exito() {
        cita.setFechaInicio(LocalDateTime.now().plusHours(2));
        cita.setEstado(EstadoCita.PENDIENTE);

        cita.confirmar();

        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    void testConfirmar_FechaPasada_LanzaExcepcion() {
        cita.setFechaInicio(LocalDateTime.now().minusHours(1));

        assertThatThrownBy(() -> cita.confirmar())
                .isInstanceOf(DomainException.class)
                .hasMessage("No se puede confirmar una cita con fecha pasada");
    }

    @Test
    void testConfirmar_FechaActual_LanzaExcepcion() {
        cita.setFechaInicio(LocalDateTime.now().minusMinutes(1));

        assertThatThrownBy(() -> cita.confirmar())
                .isInstanceOf(DomainException.class)
                .hasMessage("No se puede confirmar una cita con fecha pasada");
    }
}