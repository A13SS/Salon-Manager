package com.salon.manager.application.dto.response;

import com.salon.manager.common.enums.Rol;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioResponse {
    private Long id;
    private String email;
    private String nombre;
    private String telefono;
    private Rol rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}
