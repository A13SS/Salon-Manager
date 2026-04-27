package com.salon.manager.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuecoResponse {
    private String hora;
    private boolean disponible;
    private String estado;
}
