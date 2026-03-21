package com.salon.manager.domain.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Servicio {
    private Long id;
    private String nombre;
    private Integer duracionMin;
    private BigDecimal precio;
    private Boolean activo;

    public Servicio() {
        this.activo = true;
    }
}