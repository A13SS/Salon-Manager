package com.salon.manager.application.service;

import com.salon.manager.application.dto.response.LoginResponse;
import com.salon.manager.domain.model.Usuario;
import com.salon.manager.domain.repository.UsuarioRepository;
import com.salon.manager.infrastructure.security.JwtTokenProvider;
import com.salon.manager.infrastructure.security.UserDetailsImpl;
import com.salon.manager.logger.LoggerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider tokenProvider;
    private final LoggerServiceImpl loggerService;

    public LoginResponse login(String email, String password) {

        //Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        //Guardar
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Sacar los datos
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //Generar JWT
        String jwt = tokenProvider.generateToken(userDetails);

        //Log con ID correcto
        loggerService.logAccion("LOGIN_EXITOSO",
                "Usuario logueado: " + email,
                userDetails.getId());

        //Devolver respuesta de loguin
        return new LoginResponse(
                jwt,
                userDetails.getUsername(),
                userDetails.getAuthorities()
                        .iterator().next()
                        .getAuthority()
                        .replace("ROLE_", ""), // "ROLE_CLIENTE" "CLIENTE"
                userDetails.getId()
        );
    }
}
