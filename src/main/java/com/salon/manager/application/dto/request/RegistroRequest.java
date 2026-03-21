package com.salon.manager.application.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RegistroRequest {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email no válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    private String alergias;

    @AssertTrue(message = "Debe aceptar los términos de seguridad")
    private Boolean consentimientoSeguridad;
}
