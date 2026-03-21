package com.salon.manager.application.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;


//Los request reciben datos que el frontend envía al backend en formato JSON.
@Data
public class CrearCitaRequest {
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El profesional es obligatorio")
    private Long profesionalId;

    @NotNull(message = "El servicio es obligatorio")
    private Long servicioId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    private String observaciones;

    private String alergias;
}
