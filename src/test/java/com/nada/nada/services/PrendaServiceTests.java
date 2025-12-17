package com.nada.nada.services;

import com.nada.nada.data.model.*;
import com.nada.nada.data.model.enums.CategoriaSuperior;
import com.nada.nada.data.model.enums.Manga;
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

/**
 * Tests de integración para PrendaService.
 */
@SpringBootTest
@ActiveProfiles("test")
class PrendaServiceTests {

    @Autowired
    private PrendaService prendaService;

    @Autowired
    private PrendaRepository prendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crea y persiste un usuario de prueba.
     *
     * @param username nombre de usuario único
     * @return usuario guardado en base de datos
     */
    private Usuario crearUsuarioDePrueba(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@mail.com");
        return usuarioRepository.save(u);
    }

    /**
     * Crea una prenda superior sin persistir.
     *
     * @param u usuario propietario
     * @param nombre nombre de la prenda
     * @return prenda superior sin guardar
     */
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

    // =========================================================================
    // TESTS DE GUARDADO Y BÚSQUEDA
    // =========================================================================

    /**
     * Verifica que se puede guardar una prenda superior y buscarla por ID.
     */
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

    /**
     * Verifica que guardar una prenda sin usuario lanza excepción.
     */
    @Test
    void testGuardarPrendaSuperiorSinUsuarioLanzaExcepcion() {
        PrendaSuperior sup = nuevaSuperior(null, "SinUsuario");
        sup.setUsuario(null);
        assertThrows(IllegalArgumentException.class, () -> prendaService.guardarPrendaSuperior(sup));
    }

    /**
     * Verifica que borrar una prenda existente la elimina correctamente.
     */
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

    /**
     * Verifica que actualizar una prenda modifica sus campos correctamente.
     */
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

    /**
     * Verifica que buscar prendas por usuario devuelve solo las suyas.
     */
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

    /**
     * Verifica que contar prendas por usuario coincide con el número guardado.
     */
    @Test
    void testContarPrendasPorUsuarioCoincideConNumeroGuardadas() {
        Usuario u = crearUsuarioDePrueba("usuarioContador");
        prendaService.guardarPrendaSuperior(nuevaSuperior(u, "Camiseta A"));
        prendaService.guardarPrendaSuperior(nuevaSuperior(u, "Camiseta B"));

        long count = prendaService.contarPrendasPorUsuario(u.getId());
        assertEquals(2L, count);
    }

    /**
     * Verifica el filtrado de prendas por nombre y tipo.
     */
    @Test
    void testBuscarPrendasFiltradasPorNombreYTipo() {
        Usuario u = crearUsuarioDePrueba("usuarioFiltro");
        prendaService.guardarPrendaSuperior(nuevaSuperior(u, "Camiseta Blanca"));
        prendaService.guardarPrendaSuperior(nuevaSuperior(u, "Camiseta Negra"));

        List<Prenda> filtradas = prendaService.buscarPrendasFiltradas(u.getId(), "Camiseta B", "superior", null, null, null, null);
        assertEquals(1, filtradas.size());
        assertEquals("Camiseta Blanca", filtradas.get(0).getNombre());
    }

    // =========================================================================
    // TESTS DE NORMALIZACIÓN DE MARCAS
    // =========================================================================

    /**
     * Verifica que marcas conocidas se normalizan al formato estándar.
     */
    @Test
    void testNormalizarMarcaConocidaDevuelveFormatoEstandar() {
        assertEquals("Zara", prendaService.normalizarMarca("zara"));
        assertEquals("Zara", prendaService.normalizarMarca("ZARA"));
        assertEquals("H&M", prendaService.normalizarMarca("h&m"));
        assertEquals("H&M", prendaService.normalizarMarca("h & m"));
        assertEquals("Pull&Bear", prendaService.normalizarMarca("pull&bear"));
        assertEquals("Levi's", prendaService.normalizarMarca("levis"));
        assertEquals("Levi's", prendaService.normalizarMarca("levi's"));
    }

    /**
     * Verifica que marcas desconocidas se capitalizan correctamente.
     */
    @Test
    void testNormalizarMarcaDesconocidaCapitalizaPrimeraLetra() {
        assertEquals("Mitienda", prendaService.normalizarMarca("mitienda"));
        assertEquals("Otramarca", prendaService.normalizarMarca("OTRAMARCA"));
    }

    /**
     * Verifica que normalizar una marca nula devuelve null.
     */
    @Test
    void testNormalizarMarcaNulaDevuelveNull() {
        assertNull(prendaService.normalizarMarca(null));
    }

    /**
     * Verifica que normalizar una marca vacía devuelve cadena vacía.
     */
    @Test
    void testNormalizarMarcaVaciaDevuelveVacio() {
        assertEquals("", prendaService.normalizarMarca(""));
        assertEquals("", prendaService.normalizarMarca("   "));
    }

    // =========================================================================
    // TESTS DE VALIDACIÓN DE DATOS
    // =========================================================================

    /**
     * Verifica que una prenda sin nombre lanza excepción.
     */
    @Test
    void testValidarDatosNuevaPrendaSinNombreLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                prendaService.validarDatosNuevaPrenda(null, "superior",
                        CategoriaSuperior.CAMISETA, null, null, "Zara", "M", "Blanco"));
    }

    /**
     * Verifica que una prenda sin tipo lanza excepción.
     */
    @Test
    void testValidarDatosNuevaPrendaSinTipoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                prendaService.validarDatosNuevaPrenda("Camiseta", null,
                        CategoriaSuperior.CAMISETA, null, null, "Zara", "M", "Blanco"));
    }

    /**
     * Verifica que una prenda superior sin categoría lanza excepción.
     */
    @Test
    void testValidarDatosNuevaPrendaSuperiorSinCategoriaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                prendaService.validarDatosNuevaPrenda("Camiseta", "superior",
                        null, null, null, "Zara", "M", "Blanco"));
    }

    /**
     * Verifica que una prenda sin marca lanza excepción.
     */
    @Test
    void testValidarDatosNuevaPrendaSinMarcaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                prendaService.validarDatosNuevaPrenda("Camiseta", "superior",
                        CategoriaSuperior.CAMISETA, null, null, null, "M", "Blanco"));
    }

    /**
     * Verifica que una prenda sin talla lanza excepción.
     */
    @Test
    void testValidarDatosNuevaPrendaSinTallaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                prendaService.validarDatosNuevaPrenda("Camiseta", "superior",
                        CategoriaSuperior.CAMISETA, null, null, "Zara", null, "Blanco"));
    }

    /**
     * Verifica que una prenda sin color lanza excepción.
     */
    @Test
    void testValidarDatosNuevaPrendaSinColorLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                prendaService.validarDatosNuevaPrenda("Camiseta", "superior",
                        CategoriaSuperior.CAMISETA, null, null, "Zara", "M", null));
    }

    /**
     * Verifica que una talla inválida para prenda superior lanza excepción.
     */
    @Test
    void testValidarDatosNuevaPrendaConTallaInvalidaParaSuperiorLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                prendaService.validarDatosNuevaPrenda("Camiseta", "superior",
                        CategoriaSuperior.CAMISETA, null, null, "Zara", "99", "Blanco"));
    }

    /**
     * Verifica que datos válidos no lanzan excepción.
     */
    @Test
    void testValidarDatosNuevaPrendaValidaNoLanzaExcepcion() {
        assertDoesNotThrow(() ->
                prendaService.validarDatosNuevaPrenda("Camiseta", "superior",
                        CategoriaSuperior.CAMISETA, null, null, "Zara", "M", "Blanco"));
    }

    // =========================================================================
    // TESTS DE OBTENCIÓN DE MARCAS Y COLORES
    // =========================================================================

    /**
     * Verifica que se obtienen las marcas únicas del usuario.
     */
    @Test
    void testObtenerMarcasDelUsuarioDevuelveMarcasUnicas() {
        Usuario u = crearUsuarioDePrueba("usuarioMarcas");
        PrendaSuperior p1 = nuevaSuperior(u, "Prenda1");
        p1.setMarca("Zara");
        prendaService.guardarPrendaSuperior(p1);

        PrendaSuperior p2 = nuevaSuperior(u, "Prenda2");
        p2.setMarca("Nike");
        prendaService.guardarPrendaSuperior(p2);

        PrendaSuperior p3 = nuevaSuperior(u, "Prenda3");
        p3.setMarca("Zara"); // Repetida
        prendaService.guardarPrendaSuperior(p3);

        List<String> marcas = prendaService.obtenerMarcasDelUsuario(u.getId());
        assertNotNull(marcas);
        assertTrue(marcas.contains("Zara"));
        assertTrue(marcas.contains("Nike"));
    }

    /**
     * Verifica que obtener marcas con ID nulo devuelve lista vacía.
     */
    @Test
    void testObtenerMarcasDelUsuarioConIdNuloDevuelveListaVacia() {
        List<String> marcas = prendaService.obtenerMarcasDelUsuario(null);
        assertNotNull(marcas);
        assertTrue(marcas.isEmpty());
    }

    @Test
    void testObtenerColoresDelUsuarioDevuelveColoresUnicos() {
        Usuario u = crearUsuarioDePrueba("usuarioColores");
        PrendaSuperior p1 = nuevaSuperior(u, "Prenda1");
        p1.setColor("Rojo");
        prendaService.guardarPrendaSuperior(p1);

        PrendaSuperior p2 = nuevaSuperior(u, "Prenda2");
        p2.setColor("Azul");
        prendaService.guardarPrendaSuperior(p2);

        List<String> colores = prendaService.obtenerColoresDelUsuario(u.getId());
        assertNotNull(colores);
        assertTrue(colores.contains("Rojo"));
        assertTrue(colores.contains("Azul"));
    }

    @Test
    void testObtenerColoresDelUsuarioConIdNuloDevuelveListaVacia() {
        List<String> colores = prendaService.obtenerColoresDelUsuario(null);
        assertNotNull(colores);
        assertTrue(colores.isEmpty());
    }

    // --- Tests de borrar prenda con id inválido ---

    @Test
    void testBorrarPrendaConIdNuloDevuelveFalse() {
        assertFalse(prendaService.borrarPrenda(null));
    }

    @Test
    void testBorrarPrendaConIdNegativoDevuelveFalse() {
        assertFalse(prendaService.borrarPrenda(-1L));
    }

    @Test
    void testBorrarPrendaInexistenteDevuelveFalse() {
        assertFalse(prendaService.borrarPrenda(999999L));
    }

    // --- Tests de buscar prenda con id inválido ---

    @Test
    void testBuscarPrendaPorIdNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> prendaService.buscarPrendaPorId(null));
    }

    @Test
    void testBuscarPrendaPorIdNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> prendaService.buscarPrendaPorId(-1L));
    }

    @Test
    void testBuscarPrendaPorIdInexistenteDevuelveEmpty() {
        Optional<Prenda> resultado = prendaService.buscarPrendaPorId(999999L);
        assertTrue(resultado.isEmpty());
    }
}
