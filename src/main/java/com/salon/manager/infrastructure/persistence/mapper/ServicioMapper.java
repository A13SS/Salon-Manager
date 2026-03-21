package com.salon.manager.infrastructure.persistence.mapper;

import com.salon.manager.domain.model.Servicio;
import com.salon.manager.infrastructure.persistence.entity.ServicioEntity;
import lombok.Data;

public class ServicioMapper {

    public static Servicio toDomain(ServicioEntity entity) {
        if (entity == null) return null;
        Servicio servicio = new Servicio();
        servicio.setId(entity.getId());
        servicio.setNombre(entity.getNombre());
        servicio.setDuracionMin(entity.getDuracionMin());
        servicio.setPrecio(entity.getPrecio());
        servicio.setActivo(entity.getActivo());
        return servicio;
    }

    public static ServicioResponse toResponse(Servicio servicio) {
        if (servicio == null) return null;
        ServicioResponse response = new ServicioResponse();
        response.setId(servicio.getId());
        response.setNombre(servicio.getNombre());
        response.setDuracionMin(servicio.getDuracionMin());
        response.setPrecio(servicio.getPrecio());
        response.setActivo(servicio.getActivo());
        return response;
    }

    @Data
    public static class ServicioResponse {
        private Long id;
        private String nombre;
        private Integer duracionMin;
        private java.math.BigDecimal precio;
        private Boolean activo;
    }
}
