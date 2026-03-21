package com.salon.manager.domain.repository;

import com.salon.manager.domain.model.Usuario;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorEmail(String email);
    List<Usuario> listarTodos();
    void eliminar(Long id);
    boolean existeEmail(String email);
}