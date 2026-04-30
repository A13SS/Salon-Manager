package com.salon.manager.application.service;

import com.salon.manager.domain.model.Usuario;
import com.salon.manager.domain.repository.UsuarioRepository;
import com.salon.manager.logger.LoggerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GestorRGPD {

    private final UsuarioRepository usuarioRepository;
    private final LoggerServiceImpl loggerService;

    @Transactional
    public void borrarLogico(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuario.desactivar();
            usuarioRepository.guardar(usuario);
        } catch (Exception e) {
            loggerService.logError("Error en borrado lógico RGPD: " + e.getMessage(), usuarioId);
            throw e;
        }
    }

    @Transactional
    public void borrarFisico(Long usuarioId) {
        try {
            usuarioRepository.eliminar(usuarioId);
        } catch (Exception e) {
            loggerService.logError("Error en borrado físico RGPD: " + e.getMessage(), usuarioId);
            throw e;
        }
    }
}
