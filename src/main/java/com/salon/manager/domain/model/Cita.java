package com.salon.manager.domain.model;

import com.salon.manager.common.enums.EstadoCita;
import lombok.Data;
import java.time.LocalDateTime;
import com.salon.manager.domain.exception.DomainException;

@Data
public class Cita {
    private Long id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long clienteId;
    private Long profesionalId;
    private Long servicioId;
    private EstadoCita estado;
    private boolean atendido;
    private String alergias;
    private String observaciones;
    private LocalDateTime fechaCreacion;

    public Cita() {
        this.estado = EstadoCita.PENDIENTE;
        this.atendido = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void marcarAtendida() {
        if (this.atendido) {
            throw new DomainException("La cita ya está atendida");
        }
        this.atendido = true;
        this.estado = EstadoCita.REALIZADA;
    }

    public void cancelar() {
        if (this.estado == EstadoCita.REALIZADA) {
            throw new DomainException("No se puede cancelar una cita ya realizada");
        }
        this.estado = EstadoCita.CANCELADA;
    }

    public void confirmar() {
        if (this.fechaInicio.isBefore(LocalDateTime.now())) {
            throw new DomainException("No se puede confirmar una cita con fecha pasada");
        }
        this.estado = EstadoCita.CONFIRMADA;
    }
}
