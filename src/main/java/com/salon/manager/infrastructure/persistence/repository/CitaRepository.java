package com.salon.manager.infrastructure.persistence.repository;

import com.salon.manager.domain.model.Cita;
import com.salon.manager.infrastructure.persistence.entity.CitaEntity;
import com.salon.manager.infrastructure.persistence.mapper.CitaMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface CitaRepository extends JpaRepository<CitaEntity, Long>, com.salon.manager.domain.repository.CitaRepository {

    @Query("SELECT COUNT(c) FROM CitaEntity c " +
            "WHERE c.profesionalId = :profesionalId " +
            "AND ((c.fechaInicio < :fin AND c.fechaFin > :inicio))")
    long contarSolapes(@Param("profesionalId") Long profesionalId,
                       @Param("inicio") LocalDateTime inicio,
                       @Param("fin") LocalDateTime fin);

    List<CitaEntity> findByClienteIdOrderByFechaInicioDesc(Long clienteId);
    List<CitaEntity> findByProfesionalIdOrderByFechaInicioAsc(Long profesionalId);
    List<CitaEntity> findByFechaInicioBetweenOrderByFechaInicioAsc(LocalDateTime inicio, LocalDateTime fin);
    List<CitaEntity> findByProfesionalIdAndFechaInicioBetween(Long profesionalId, LocalDateTime inicio, LocalDateTime fin);

    @Override
    default Cita guardar(Cita cita) {
        CitaEntity entity = CitaMapper.toEntity(cita);
        return CitaMapper.toDomain(save(entity));
    }

    @Override
    default Optional<Cita> buscarPorId(Long id) {
        return findById(id).map(CitaMapper::toDomain);
    }

    @Override
    default List<Cita> buscarPorCliente(Long clienteId) {
        return findByClienteIdOrderByFechaInicioDesc(clienteId).stream()
                .map(CitaMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    default List<Cita> buscarPorProfesional(Long profesionalId) {
        return findByProfesionalIdOrderByFechaInicioAsc(profesionalId).stream()
                .map(CitaMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    default List<Cita> buscarPorFecha(LocalDateTime fecha) {
        LocalDateTime inicioDia = fecha.toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);
        return findByFechaInicioBetweenOrderByFechaInicioAsc(inicioDia, finDia)
                .stream()
                .map(CitaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    default List<Cita> buscarPorProfesionalYFecha(Long profesionalId, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(23, 59, 59);

        return findByProfesionalIdAndFechaInicioBetween(profesionalId, inicio, fin).stream()
                .map(CitaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    default void eliminar(Long id) {
        deleteById(id);
    }
}
