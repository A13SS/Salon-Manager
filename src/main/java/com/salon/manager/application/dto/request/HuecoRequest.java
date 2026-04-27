package com.salon.manager.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuecoRequest {
    private String hora;
    private boolean disponible;
    private String estado;
}
