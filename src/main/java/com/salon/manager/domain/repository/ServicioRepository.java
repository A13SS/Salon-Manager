package com.salon.manager.domain.repository;

import com.salon.manager.domain.model.Servicio;
import java.util.List;
import java.util.Optional;

public interface ServicioRepository {
    List<Servicio> listarActivos();
    Optional<Servicio> buscarPorId(Long id);
}
