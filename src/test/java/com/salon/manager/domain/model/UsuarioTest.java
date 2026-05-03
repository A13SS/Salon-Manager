package com.salon.manager.domain.model;

import com.salon.manager.common.enums.Rol;
import com.salon.manager.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@email.com");
        usuario.setPassword("password123");
        usuario.setNombre("Test User");
        usuario.setTelefono("600123456");
        usuario.setFechaRegistro(LocalDateTime.now());
    }

    @Test
    void testCrearUsuario_ValoresPorDefecto() {
        Usuario nuevoUsuario = new Usuario();

        assertThat(nuevoUsuario.getRol()).isEqualTo(Rol.CLIENTE);
        assertThat(nuevoUsuario.getActivo()).isTrue();
    }

    @Test
    void testDesactivar_Exito() {
        assertThat(usuario.getActivo()).isTrue();

        usuario.desactivar();

        assertThat(usuario.getActivo()).isFalse();
    }

    @Test
    void testDesactivar_YaDesactivado_LanzaExcepcion() {
        usuario.setActivo(false);

        assertThatThrownBy(() -> usuario.desactivar())
                .isInstanceOf(DomainException.class)
                .hasMessage("El usuario ya está desactivado");
    }

    @Test
    void testCambiarRol_Exito() {
        assertThat(usuario.getRol()).isEqualTo(Rol.CLIENTE);

        usuario.cambiarRol(Rol.PROFESIONAL);

        assertThat(usuario.getRol()).isEqualTo(Rol.PROFESIONAL);

        usuario.cambiarRol(Rol.ADMIN);

        assertThat(usuario.getRol()).isEqualTo(Rol.ADMIN);
    }

    @Test
    void testCambiarRol_Null_LanzaExcepcion() {
        assertThatThrownBy(() -> usuario.cambiarRol(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("El rol no puede ser nulo");
    }

    @Test
    void testPuedeAcceder_Activo_ReturnsTrue() {
        usuario.setActivo(true);

        boolean puedeAcceder = usuario.puedeAcceder();

        assertThat(puedeAcceder).isTrue();
    }

    @Test
    void testPuedeAcceder_Inactivo_LanzaExcepcion() {
        usuario.setActivo(false);

        assertThatThrownBy(() -> usuario.puedeAcceder())
                .isInstanceOf(DomainException.class)
                .hasMessage("El usuario no está activo y no puede acceder");
    }

    @Test
    void testSetters_Getters() {
        Long id = 2L;
        String email = "nuevo@email.com";
        String password = "newPass456";
        Rol rol = Rol.PROFESIONAL;
        String nombre = "Nuevo Nombre";
        String telefono = "611222333";

        usuario.setId(id);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setRol(rol);
        usuario.setNombre(nombre);
        usuario.setTelefono(telefono);

        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getEmail()).isEqualTo(email);
        assertThat(usuario.getPassword()).isEqualTo(password);
        assertThat(usuario.getRol()).isEqualTo(rol);
        assertThat(usuario.getNombre()).isEqualTo(nombre);
        assertThat(usuario.getTelefono()).isEqualTo(telefono);
    }

    @Test
    void testEmail_Vacio() {
        usuario.setEmail("");

        assertThat(usuario.getEmail()).isEmpty();
    }

    @Test
    void testTelefono_Vacio() {
        usuario.setTelefono("");

        assertThat(usuario.getTelefono()).isEmpty();
    }
}