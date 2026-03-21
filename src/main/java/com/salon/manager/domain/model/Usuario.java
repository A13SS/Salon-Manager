package com.salon.manager.domain.model;
import com.salon.manager.common.enums.Rol;
import com.salon.manager.domain.exception.DomainException;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Usuario {
    private Long id;
    private String email;
    private String password;
    private Rol rol;
    private String nombre;
    private String telefono;
    private Boolean activo;
    private LocalDateTime fechaRegistro;

    public Usuario() {
        this.activo = true;
        this.rol = Rol.CLIENTE;
    }

    public void desactivar() {
        if (!this.activo) {
            throw new DomainException("El usuario ya está desactivado");
        }
        this.activo = false;
    }

    public void cambiarRol(Rol nuevoRol) {
        if (nuevoRol == null) {
            throw new DomainException("El rol no puede ser nulo");
        }
        this.rol = nuevoRol;
    }

    public boolean puedeAcceder() {
        if (!this.activo) {
            throw new DomainException("El usuario no está activo y no puede acceder");
        }
        return Boolean.TRUE.equals(this.activo);
    }

}
