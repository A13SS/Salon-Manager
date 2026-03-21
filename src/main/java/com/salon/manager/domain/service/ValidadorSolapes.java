package com.salon.manager.domain.service;

import com.salon.manager.common.exception.SolapeException;
import com.salon.manager.domain.repository.CitaRepository;
import java.time.LocalDateTime;

public class ValidadorSolapes {

    private final CitaRepository citaRepository;

    public ValidadorSolapes(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public void validar(Long profesionalId, LocalDateTime inicio, LocalDateTime fin) {
        long solapes = citaRepository.contarSolapes(profesionalId, inicio, fin);
        if (solapes > 0) {
            throw new SolapeException("Horario no disponible. Existe solape en la hora de la cita con el profesional elegido, lo siento... intentelo de nuevo");
        }
    }
}
