package com.salon.manager.infrastructure.persistence.repository;

import com.salon.manager.domain.model.Usuario;
import com.salon.manager.infrastructure.persistence.entity.UsuarioEntity;
import com.salon.manager.infrastructure.persistence.mapper.UsuarioMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//Como uso una interfaz que extiende de JpaRepository, Spring crea la implementación directamente. Se ahorra tiempo en vez de hacer implementaciones de los repositorios.
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long>, com.salon.manager.domain.repository.UsuarioRepository {

    Optional<UsuarioEntity> findByEmailAndActivoTrue(String email);
    boolean existsByEmail(String email);

    @Override
    default Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = UsuarioMapper.toEntity(usuario);
        return UsuarioMapper.toDomain(save(entity));
    }

    @Override
    default Optional<Usuario> buscarPorId(Long id) {
        return findById(id).map(UsuarioMapper::toDomain);
    }

    @Override
    default Optional<Usuario> buscarPorEmail(String email) {
        return findByEmailAndActivoTrue(email).map(UsuarioMapper::toDomain);
    }

    @Override
    default List<Usuario> listarTodos() {
        return findAll().stream().map(UsuarioMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    default void eliminar(Long id) {
        deleteById(id);
    }

    @Override
    default boolean existeEmail(String email) {
        return existsByEmail(email);
    }
}
