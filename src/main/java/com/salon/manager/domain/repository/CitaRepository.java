package com.salon.manager.domain.repository;

import com.salon.manager.domain.model.Cita;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepository {
    Cita guardar(Cita cita);
    Optional<Cita> buscarPorId(Long id);
    List<Cita> buscarPorCliente(Long clienteId);
    List<Cita> buscarPorProfesional(Long profesionalId);
    long contarSolapes(Long profesionalId, LocalDateTime inicio, LocalDateTime fin);
    void eliminar(Long id);
}
