package com.salon.manager.application.service;

import com.salon.manager.application.dto.response.UsuarioResponse;
import com.salon.manager.common.enums.Rol;
import com.salon.manager.common.exception.ResourceNotFoundException;
import com.salon.manager.domain.model.Usuario;
import com.salon.manager.domain.repository.UsuarioRepository;
import com.salon.manager.infrastructure.persistence.mapper.UsuarioMapper;
import com.salon.manager.logger.LoggerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioAppService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GestorRGPD gestorRGPD;
    private final LoggerServiceImpl loggerService;

    @Transactional
    public UsuarioResponse registrar(String email, String password,
                                     String nombre, String telefono) {

        if (usuarioRepository.existeEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setNombre(nombre);
        usuario.setTelefono(telefono);
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        usuario.setFechaRegistro(java.time.LocalDateTime.now());

        Usuario guardado = usuarioRepository.guardar(usuario);

        //Registro un log exitoso
        loggerService.logAccion("REGISTRO_USUARIO",
                "Usuario registrado: " + email,
                guardado.getId());
        return UsuarioMapper.toResponse(guardado);
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.listarTodos().stream()
                .map(UsuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + id));

        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void borrarLogico(Long id) {
        //Antes de borrar de manera lógica
        loggerService.logAccion("BORRADO_LOGICO_USUARIO",
                "Borrado lógico solicitado",
                id);
        gestorRGPD.borrarLogico(id);
    }

    @Transactional
    public void borrarFisico(Long id) {
        //Antes de borrar
        loggerService.logAccion("BORRADO_FISICO_USUARIO",
                "Borrado físico solicitado (RGPD)",
                id);
        gestorRGPD.borrarFisico(id);
    }

    @Transactional
    public void cambiarRol(Long id, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        loggerService.logAccion("CAMBIO_ROL",
                "Cambio de rol de " + usuario.getRol() + " a " + nuevoRol,
                id);

        usuario.cambiarRol(nuevoRol);
        usuarioRepository.guardar(usuario);
    }
}
