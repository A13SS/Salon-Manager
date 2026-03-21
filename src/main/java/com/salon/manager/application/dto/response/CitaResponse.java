package com.salon.manager.application.dto.response;

import com.salon.manager.common.enums.EstadoCita;
import lombok.Data;
import java.time.LocalDateTime;

//Los response envian datos del backend al frontend en formato JSON
@Data
public class CitaResponse {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long profesionalId;
    private String profesionalNombre;
    private Long servicioId;
    private String servicioNombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoCita estado;
    private Boolean atendido;
    private String alergias;
    private String observaciones;
}
