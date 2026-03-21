package com.salon.manager.infrastructure.persistence.mapper;

import com.salon.manager.application.dto.response.UsuarioResponse;
import com.salon.manager.domain.model.Usuario;
import com.salon.manager.infrastructure.persistence.entity.UsuarioEntity;

public class UsuarioMapper {

    public static Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        Usuario usuario = new Usuario();
        usuario.setId(entity.getId());
        usuario.setEmail(entity.getEmail());
        usuario.setPassword(entity.getPassword());
        usuario.setRol(entity.getRol());
        usuario.setNombre(entity.getNombre());
        usuario.setTelefono(entity.getTelefono());
        usuario.setActivo(entity.getActivo());
        usuario.setFechaRegistro(entity.getFechaRegistro());
        return usuario;
    }

    public static UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRol(domain.getRol());
        entity.setNombre(domain.getNombre());
        entity.setTelefono(domain.getTelefono());
        entity.setActivo(domain.getActivo());
        entity.setFechaRegistro(domain.getFechaRegistro());
        return entity;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setEmail(usuario.getEmail());
        response.setNombre(usuario.getNombre());
        response.setTelefono(usuario.getTelefono());
        response.setRol(usuario.getRol());
        response.setActivo(usuario.getActivo());
        response.setFechaRegistro(usuario.getFechaRegistro());

        return response;
    }
}
