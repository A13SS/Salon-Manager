package com.salon.manager.application.service;

import com.salon.manager.application.dto.response.LoginResponse;
import com.salon.manager.infrastructure.security.JwtTokenProvider;
import com.salon.manager.infrastructure.security.UserDetailsImpl;
import com.salon.manager.logger.LoggerServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private LoggerServiceImpl loggerService;

    @InjectMocks
    private AuthService authService;

    private UserDetailsImpl userDetails;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        //Mock de UserDetailsImpl
        userDetails = mock(UserDetailsImpl.class);
        lenient().when(userDetails.getId()).thenReturn(1L);
        lenient().when(userDetails.getUsername()).thenReturn("test@email.com");

        //Devuelve una lista con un solo rol: ROLE_CLIENTE
        lenient().doReturn(List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                .when(userDetails).getAuthorities();

        //Mock de Authentication
        authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);

        //Crear mock de SecurityContext
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        //Limpiar SecurityContextHolder después de cada test
        SecurityContextHolder.clearContext();
    }

    @Test
    void testLogin_Exito() {
        String email = "test@email.com";
        String password = "password123";
        String expectedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSJ9";

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(tokenProvider.generateToken(userDetails))
                .thenReturn(expectedToken);

        LoginResponse response = authService.login(email, password);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);
        assertThat(response.getEmail()).isEqualTo("test@email.com");
        assertThat(response.getRol()).isEqualTo("CLIENTE");
        assertThat(response.getUserId()).isEqualTo(1L);

        //Verificar interacciones
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(tokenProvider, times(1)).generateToken(userDetails);

        verify(loggerService, times(1))
                .logAccion(eq("LOGIN_EXITOSO"), anyString(), eq(1L));

        //Verificar que se llamó a setAuthentication
        verify(securityContext, times(1)).setAuthentication(authentication);
    }

    @Test
    void testLogin_CredencialesInvalidas_LanzaExcepcion() {
        String email = "wrong@email.com";
        String password = "wrongpass";

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));
        assertThatThrownBy(() -> authService.login(email, password))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credenciales inválidas");

        //Verificar que NO se generó token ni se hizo log de éxito
        verify(tokenProvider, never()).generateToken(any());
        verify(loggerService, never())
                .logAccion(eq("LOGIN_EXITOSO"), anyString(), anyLong());
    }

    @Test
    void testLogin_RolProfesional_SinPrefijo() {
        lenient().doReturn(List.of(new SimpleGrantedAuthority("ROLE_PROFESIONAL")))
                .when(userDetails).getAuthorities();

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(userDetails))
                .thenReturn("token.profesional");

        LoginResponse response = authService.login("pro@email.com", "pass");

        assertThat(response.getRol()).isEqualTo("PROFESIONAL");
    }

    @Test
    void testLogin_RolAdmin_SinPrefijo() {
        lenient().doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(userDetails).getAuthorities();

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(userDetails))
                .thenReturn("token.admin");

        LoginResponse response = authService.login("admin@email.com", "pass");

        assertThat(response.getRol()).isEqualTo("ADMIN");
    }

    @Test
    void testLogin_EstableceSecurityContext() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(userDetails))
                .thenReturn("token");

        authService.login("test@email.com", "pass");

        //Verificar que se llamó a setAuthentication
        verify(securityContext, times(1)).setAuthentication(authentication);
    }

    @Test
    void testLogin_LogConIdCorrecto() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(userDetails))
                .thenReturn("token");

        authService.login("test@email.com", "pass");

        verify(loggerService, times(1))
                .logAccion(
                        eq("LOGIN_EXITOSO"),
                        eq("Usuario logueado: test@email.com"),
                        eq(1L)
                );
    }
}