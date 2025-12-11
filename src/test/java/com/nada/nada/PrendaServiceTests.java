package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.UsuarioRepository;
import com.nada.nada.data.services.PrendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PrendaServiceTests {

    @Autowired
    private PrendaService prendaService;

    @Autowired
    private PrendaRepository prendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crea y guarda un usuario de prueba para los tests de prendas
    private Usuario crearUsuarioDePrueba(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@mail.com");
        return usuarioRepository.save(u);
    }

    private PrendaSuperior nuevaSuperior(Usuario u, String nombre) {
        PrendaSuperior p = new PrendaSuperior();
        p.setNombre(nombre);
        p.setColor("Blanco");
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("M");
        p.setDirImagen("images/sup.png");
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setManga(Manga.CORTA);
        return p;
    }

    @Test
    void testGuardarYBuscarPrendaSuperiorPorId() {
        Usuario u = crearUsuarioDePrueba("usuarioPs");
        PrendaSuperior sup = nuevaSuperior(u, "Camiseta Servicio");
        PrendaSuperior guardada = prendaService.guardarPrendaSuperior(sup);

        Optional<Prenda> rec = prendaService.buscarPrendaPorId(guardada.getId());
        assertTrue(rec.isPresent());
        assertEquals("Camiseta Servicio", rec.get().getNombre());
        assertTrue(rec.get() instanceof PrendaSuperior);
    }

    @Test
    void testGuardarPrendaSuperiorSinUsuarioLanzaExcepcion() {
        PrendaSuperior sup = nuevaSuperior(null, "SinUsuario");
        sup.setUsuario(null);
        assertThrows(IllegalArgumentException.class, () -> prendaService.guardarPrendaSuperior(sup));
    }

    @Test
    void testBorrarPrendaExistenteDevuelveTrueYLaElimina() {
        Usuario u = crearUsuarioDePrueba("usuarioBorrar");
        PrendaSuperior sup = nuevaSuperior(u, "Prenda A Borrar");
        PrendaSuperior guardada = prendaService.guardarPrendaSuperior(sup);
        Long id = guardada.getId();

        boolean ok = prendaService.borrarPrenda(id);
        assertTrue(ok);
        assertFalse(prendaRepository.existsById(id));
    }

    @Test
    void testActualizarPrendaModificaCamposBasicos() {
        Usuario u = crearUsuarioDePrueba("usuarioActualizar");
        PrendaSuperior sup = nuevaSuperior(u, "Prenda Editable");
        PrendaSuperior guardada = prendaService.guardarPrendaSuperior(sup);

        guardada.setColor("Rojo");
        guardada.setMarca("Otra Marca");
        Prenda actualizada = prendaService.actualizarPrenda(guardada);

        Optional<Prenda> rec = prendaRepository.findById(actualizada.getId());
        assertTrue(rec.isPresent());
        assertEquals("Rojo", rec.get().getColor());
        assertEquals("Otra Marca", rec.get().getMarca());
    }

    @Test
    void testBuscarPrendasPorUsuarioIdDevuelveSoloLasSuyas() {
        Usuario u1 = crearUsuarioDePrueba("usuarioPropietario1");
        Usuario u2 = crearUsuarioDePrueba("usuarioPropietario2");

        prendaService.guardarPrendaSuperior(nuevaSuperior(u1, "Camiseta 1"));
        prendaService.guardarPrendaSuperior(nuevaSuperior(u1, "Camiseta 2"));
        prendaService.guardarPrendaSuperior(nuevaSuperior(u2, "Camiseta 3"));

        List<Prenda> prendasU1 = prendaService.buscarPrendasPorUsuarioId(u1.getId());
        assertTrue(prendasU1.stream().allMatch(p -> p.getUsuario().getId().equals(u1.getId())));
        assertEquals(2, prendasU1.size());
    }

    @Test
    void testContarPrendasPorUsuarioCoincideConNumeroGuardadas() {
        Usuario u = crearUsuarioDePrueba("usuarioContador");
        prendaService.guardarPrendaSuperior(nuevaSuperior(u, "Camiseta A"));
        prendaService.guardarPrendaSuperior(nuevaSuperior(u, "Camiseta B"));

        long count = prendaService.contarPrendasPorUsuario(u.getId());
        assertEquals(2L, count);
    }

    @Test
    void testBuscarPrendasFiltradasPorNombreYTipo() {
        Usuario u = crearUsuarioDePrueba("usuarioFiltro");
        prendaService.guardarPrendaSuperior(nuevaSuperior(u, "Camiseta Blanca"));
        prendaService.guardarPrendaSuperior(nuevaSuperior(u, "Camiseta Negra"));

        List<Prenda> filtradas = prendaService.buscarPrendasFiltradas(u.getId(), "Camiseta B", "superior", null, null, null, null);
        assertEquals(1, filtradas.size());
        assertEquals("Camiseta Blanca", filtradas.get(0).getNombre());
    }

    @Test
    void testNormalizarColorConValoresDelEnum() {
        assertEquals("Blanco", prendaService.normalizarColor("blanco"));
        assertEquals("Blanco", prendaService.normalizarColor("BLANCO"));
        assertEquals("Blanco", prendaService.normalizarColor("Blanco"));
        assertEquals("Negro", prendaService.normalizarColor("negro"));
        assertEquals("Azul marino", prendaService.normalizarColor("azul marino"));
        assertEquals("Azul marino", prendaService.normalizarColor("AZUL MARINO"));
    }

    @Test
    void testNormalizarColorConValorDesconocido() {
        assertEquals("Personalizado", prendaService.normalizarColor("personalizado"));
        assertEquals("Nuevo", prendaService.normalizarColor("NUEVO"));
    }

    @Test
    void testNormalizarColorConValoresNulosOVacios() {
        assertNull(prendaService.normalizarColor(null));
        assertEquals("", prendaService.normalizarColor(""));
        assertEquals("", prendaService.normalizarColor("   "));
    }

    @Test
    void testNormalizarMarcaConValoresDelEnum() {
        assertEquals("Zara", prendaService.normalizarMarca("zara"));
        assertEquals("Zara", prendaService.normalizarMarca("ZARA"));
        assertEquals("Zara", prendaService.normalizarMarca("Zara"));
        assertEquals("Nike", prendaService.normalizarMarca("nike"));
        assertEquals("H&M", prendaService.normalizarMarca("h&m"));
        assertEquals("Levi's", prendaService.normalizarMarca("levis"));
    }

    @Test
    void testNormalizarMarcaConValorDesconocido() {
        assertEquals("Personalizado", prendaService.normalizarMarca("personalizado"));
        assertEquals("Nueva", prendaService.normalizarMarca("NUEVA"));
    }

    @Test
    void testNormalizarMarcaConValoresNulosOVacios() {
        assertNull(prendaService.normalizarMarca(null));
        assertEquals("", prendaService.normalizarMarca(""));
        assertEquals("", prendaService.normalizarMarca("   "));
    }
}
