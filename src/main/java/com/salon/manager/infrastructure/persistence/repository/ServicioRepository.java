package com.salon.manager.infrastructure.persistence.repository;

import com.salon.manager.domain.model.Servicio;
import com.salon.manager.infrastructure.persistence.entity.ServicioEntity;
import com.salon.manager.infrastructure.persistence.mapper.ServicioMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface ServicioRepository extends JpaRepository<ServicioEntity, Long>, com.salon.manager.domain.repository.ServicioRepository {

    List<ServicioEntity> findByActivoTrue();

    @Override
    default List<Servicio> listarActivos() {
        return findByActivoTrue().stream().map(ServicioMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    default Optional<Servicio> buscarPorId(Long id) {
        return findById(id).map(ServicioMapper::toDomain);
    }
}
