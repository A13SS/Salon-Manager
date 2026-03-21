package com.salon.manager.application.service;

import com.salon.manager.domain.model.Usuario;
import com.salon.manager.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GestorRGPD {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void borrarLogico(Long usuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.desactivar();
        usuarioRepository.guardar(usuario);
    }

    @Transactional
    public void borrarFisico(Long usuarioId) {
        usuarioRepository.eliminar(usuarioId);
    }
}
