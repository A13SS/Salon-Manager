package com.salon.manager.application.dto.response;

import lombok.Data;

@Data
public class ServicioResponse {
    private Long id;
    private String nombre;
    private Integer duracionMin;
    private java.math.BigDecimal precio;
    private Boolean activo;
}
