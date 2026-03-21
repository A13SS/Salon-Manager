package com.salon.manager.infrastructure.persistence.mapper;
import com.salon.manager.application.dto.response.CitaResponse;
import com.salon.manager.domain.model.Cita;
import com.salon.manager.infrastructure.persistence.entity.CitaEntity;

public class CitaMapper {

    public static Cita toDomain(CitaEntity entity) {
        if (entity == null) return null;
        Cita cita = new Cita();
        cita.setId(entity.getId());
        cita.setFechaInicio(entity.getFechaInicio());
        cita.setFechaFin(entity.getFechaFin());
        cita.setClienteId(entity.getClienteId());
        cita.setProfesionalId(entity.getProfesionalId());
        cita.setServicioId(entity.getServicioId());
        cita.setEstado(entity.getEstado());
        cita.setAtendido(entity.getAtendido());
        cita.setAlergias(entity.getAlergias());
        cita.setObservaciones(entity.getObservaciones());
        cita.setFechaCreacion(entity.getFechaCreacion());
        return cita;
    }

    public static CitaEntity toEntity(Cita domain) {
        if (domain == null) return null;
        CitaEntity entity = new CitaEntity();
        entity.setId(domain.getId());
        entity.setFechaInicio(domain.getFechaInicio());
        entity.setFechaFin(domain.getFechaFin());
        entity.setClienteId(domain.getClienteId());
        entity.setProfesionalId(domain.getProfesionalId());
        entity.setServicioId(domain.getServicioId());
        entity.setEstado(domain.getEstado());
        entity.setAtendido(domain.isAtendido());
        entity.setAlergias(domain.getAlergias());
        entity.setObservaciones(domain.getObservaciones());
        entity.setFechaCreacion(domain.getFechaCreacion());
        return entity;
    }

    public static CitaResponse toResponse(Cita cita) {
        if (cita == null) return null;
        CitaResponse response = new CitaResponse();
        response.setId(cita.getId());
        response.setClienteId(cita.getClienteId());
        response.setProfesionalId(cita.getProfesionalId());
        response.setServicioId(cita.getServicioId());
        response.setFechaInicio(cita.getFechaInicio());
        response.setFechaFin(cita.getFechaFin());
        response.setEstado(cita.getEstado());
        response.setAtendido(cita.isAtendido());
        response.setAlergias(cita.getAlergias());
        response.setObservaciones(cita.getObservaciones());
        return response;
    }
}
