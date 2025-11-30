package com.nada.nada;

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

@SpringBootTest
@ActiveProfiles("test")
class UsuarioServiceTests {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario nuevoUsuario(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@gmail.com");
        return u;
    }

    @Test
    void testCrearUsuarioValidoYBuscarTodos() {
        // "usuarioServ1" tiene 12 caracteres, cumple la validación (<= 15)
        Usuario u = nuevoUsuario("usuarioServ1");
        Usuario guardado = usuarioService.crearUsuario(u);

        assertNotNull(guardado.getId());

        List<Usuario> usuarios = usuarioService.buscarTodos();
        assertTrue(usuarios.stream().anyMatch(us -> us.getUsername().equals("usuarioServ1")));
    }

    @Test
    void testCrearUsuarioDuplicadoPorUsernameLanzaExcepcion() {
        Usuario u1 = nuevoUsuario("usuarioDup");
        usuarioService.crearUsuario(u1);

        Usuario u2 = nuevoUsuario("usuarioDup");
        assertThrows(IllegalArgumentException.class, () -> usuarioService.crearUsuario(u2));
    }

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

    @Test
    void testCambiarPasswordConOldIncorrectoLanzaExcepcion() {
        Usuario u = nuevoUsuario("usuarioPwdErr");
        u.setPassword("antigua");
        Usuario guardado = usuarioService.crearUsuario(u);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.cambiarPassword(guardado.getId(), "otra", "nueva"));
    }

    @Test
    void testEliminarUsuarioEliminaDeLaBaseDeDatos() {
        Usuario u = nuevoUsuario("usuarioBorrar");
        Usuario guardado = usuarioService.crearUsuario(u);
        Long id = guardado.getId();

        usuarioService.eliminarUsuario(id);

        assertFalse(usuarioRepository.existsById(id));
    }
}
