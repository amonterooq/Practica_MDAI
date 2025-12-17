package com.nada.nada.services;

import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.UsuarioRepository;
import com.nada.nada.data.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para UsuarioService.
 */
@SpringBootTest
@ActiveProfiles("test")
class UsuarioServiceTests {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo usuario con datos básicos para testing.
     *
     * @param username nombre de usuario único
     * @return usuario sin persistir
     */
    private Usuario nuevoUsuario(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@gmail.com");
        return u;
    }

    /**
     * Verifica que se puede crear un usuario válido y aparece en la lista de todos.
     */
    @Test
    void testCrearUsuarioValidoYBuscarTodos() {
        // "usuarioServ1" tiene 12 caracteres, cumple la validación (<= 15)
        Usuario u = nuevoUsuario("usuarioServ1");
        Usuario guardado = usuarioService.crearUsuario(u);

        assertNotNull(guardado.getId());

        List<Usuario> usuarios = usuarioService.buscarTodos();
        assertTrue(usuarios.stream().anyMatch(us -> us.getUsername().equals("usuarioServ1")));
    }

    /**
     * Verifica que crear un usuario con username duplicado lanza excepción.
     */
    @Test
    void testCrearUsuarioDuplicadoPorUsernameLanzaExcepcion() {
        Usuario u1 = nuevoUsuario("usuarioDup");
        usuarioService.crearUsuario(u1);

        Usuario u2 = nuevoUsuario("usuarioDup");
        assertThrows(IllegalArgumentException.class, () -> usuarioService.crearUsuario(u2));
    }

    /**
     * Verifica la validación de login con credenciales correctas e incorrectas.
     */
    @Test
    void testValidarLoginCorrectoEIncorrecto() {
        Usuario u = nuevoUsuario("usuarioLogin");
        u.setPassword("secreto");
        usuarioService.crearUsuario(u);

        Optional<Usuario> ok = usuarioService.validarLogin("usuarioLogin", "secreto");
        assertTrue(ok.isPresent());

        Optional<Usuario> fail = usuarioService.validarLogin("usuarioLogin", "incorrecta");
        assertTrue(fail.isEmpty());
    }

    /**
     * Verifica que se puede cambiar la contraseña correctamente.
     */
    @Test
    void testCambiarPasswordCorrectamente() {
        Usuario u = nuevoUsuario("usuarioPwd");
        u.setPassword("antigua");
        Usuario guardado = usuarioService.crearUsuario(u);

        usuarioService.cambiarPassword(guardado.getId(), "antigua", "nueva");

        Optional<Usuario> recargado = usuarioRepository.findByUsername("usuarioPwd");
        assertTrue(recargado.isPresent());
        assertEquals("nueva", recargado.get().getPassword());
    }

    /**
     * Verifica que cambiar la contraseña con la antigua incorrecta lanza excepción.
     */
    @Test
    void testCambiarPasswordConOldIncorrectoLanzaExcepcion() {
        Usuario u = nuevoUsuario("usuarioPwdErr");
        u.setPassword("antigua");
        Usuario guardado = usuarioService.crearUsuario(u);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.cambiarPassword(guardado.getId(), "otra", "nueva"));
    }

    /**
     * Verifica que eliminar un usuario lo elimina de la base de datos.
     */
    @Test
    void testEliminarUsuarioEliminaDeLaBaseDeDatos() {
        Usuario u = nuevoUsuario("usuarioBorrar");
        Usuario guardado = usuarioService.crearUsuario(u);
        Long id = guardado.getId();

        usuarioService.eliminarUsuario(id);

        assertFalse(usuarioRepository.existsById(id));
    }
}
