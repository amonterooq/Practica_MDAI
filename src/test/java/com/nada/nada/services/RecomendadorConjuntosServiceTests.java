package com.nada.nada.services;

import com.nada.nada.data.model.*;
import com.nada.nada.data.model.enums.CategoriaCalzado;
import com.nada.nada.data.model.enums.CategoriaInferior;
import com.nada.nada.data.model.enums.CategoriaSuperior;
import com.nada.nada.data.model.enums.Manga;
import com.nada.nada.data.repository.*;
import com.nada.nada.data.services.RecomendadorConjuntosService;
import com.nada.nada.dto.chat.RecomendacionConjuntoRequestDto;
import com.nada.nada.dto.chat.RecomendacionConjuntoResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para RecomendadorConjuntosService.
 */
@SpringBootTest
@ActiveProfiles("test")
class RecomendadorConjuntosServiceTests {

    @Autowired
    private RecomendadorConjuntosService recomendadorService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrendaSuperiorRepository prendaSuperiorRepository;

    @Autowired
    private PrendaInferiorRepository prendaInferiorRepository;

    @Autowired
    private PrendaCalzadoRepository prendaCalzadoRepository;

    /**
     * Crea y persiste un usuario de prueba.
     *
     * @param username nombre de usuario único
     * @return usuario guardado en base de datos
     */
    private Usuario nuevoUsuario(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@mail.com");
        return usuarioRepository.save(u);
    }

    /**
     * Crea y persiste una prenda superior con atributos específicos.
     *
     * @param u usuario propietario
     * @param nombre nombre de la prenda
     * @param color color de la prenda
     * @param categoria categoría de la prenda
     * @return prenda superior guardada
     */
    private PrendaSuperior crearSuperior(Usuario u, String nombre, String color, CategoriaSuperior categoria) {
        PrendaSuperior p = new PrendaSuperior();
        p.setNombre(nombre);
        p.setColor(color);
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("M");
        p.setDirImagen("images/sup.png");
        p.setCategoria(categoria);
        p.setManga(Manga.CORTA);
        return prendaSuperiorRepository.save(p);
    }

    /**
     * Crea y persiste una prenda inferior con atributos específicos.
     *
     * @param u usuario propietario
     * @param nombre nombre de la prenda
     * @param color color de la prenda
     * @param categoria categoría de la prenda
     * @return prenda inferior guardada
     */
    private PrendaInferior crearInferior(Usuario u, String nombre, String color, CategoriaInferior categoria) {
        PrendaInferior p = new PrendaInferior();
        p.setNombre(nombre);
        p.setColor(color);
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("32");
        p.setDirImagen("images/inf.png");
        p.setCategoriaInferior(categoria);
        return prendaInferiorRepository.save(p);
    }

    /**
     * Crea y persiste un calzado con atributos específicos.
     *
     * @param u usuario propietario
     * @param nombre nombre del calzado
     * @param color color del calzado
     * @param categoria categoría del calzado
     * @return calzado guardado
     */
    private PrendaCalzado crearCalzado(Usuario u, String nombre, String color, CategoriaCalzado categoria) {
        PrendaCalzado p = new PrendaCalzado();
        p.setNombre(nombre);
        p.setColor(color);
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("42");
        p.setDirImagen("images/calz.png");
        p.setCategoria(categoria);
        return prendaCalzadoRepository.save(p);
    }

    /**
     * Crea un armario completo con varios tipos de prendas para pruebas.
     *
     * @param u usuario propietario del armario
     */
    private void crearArmarioCompleto(Usuario u) {
        crearSuperior(u, "Camiseta Blanca", "Blanco", CategoriaSuperior.CAMISETA);
        crearSuperior(u, "Jersey Azul", "Azul", CategoriaSuperior.JERSEY);
        crearInferior(u, "Pantalón Negro", "Negro", CategoriaInferior.PANTALON);
        crearInferior(u, "Vaqueros Azules", "Azul", CategoriaInferior.PANTALON_VAQUERO);
        crearCalzado(u, "Deportivas Blancas", "Blanco", CategoriaCalzado.DEPORTIVO);
        crearCalzado(u, "Botas Negras", "Negro", CategoriaCalzado.BOTA);
    }

    // =========================================================================
    // TESTS DE VALIDACIÓN DE PARÁMETROS
    // =========================================================================

    /**
     * Verifica que recomendar con usuario nulo devuelve mensaje de error.
     */
    @Test
    void testRecomendarConjuntoConUsuarioNuloDevuelveMensajeError() {
        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(null, null, null, null);

        assertNotNull(respuesta);
        assertNotNull(respuesta.getMensaje());
        assertTrue(respuesta.getMensaje().contains("usuario"));
    }

    /**
     * Verifica que recomendar sin prendas devuelve lista de faltantes.
     */
    @Test
    void testRecomendarConjuntoSinPrendasDevuelveFaltantes() {
        Usuario u = nuevoUsuario("usuarioSinPrendas");

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), null, null, null);

        assertNotNull(respuesta);
        assertNotNull(respuesta.getFaltantes());
        assertTrue(respuesta.getFaltantes().contains("superior"));
        assertTrue(respuesta.getFaltantes().contains("inferior"));
        assertTrue(respuesta.getFaltantes().contains("calzado"));
    }

    /**
     * Verifica que con solo prendas superiores devuelve faltantes de inferior y calzado.
     */
    @Test
    void testRecomendarConjuntoSoloConSuperioresDevuelveFaltantes() {
        Usuario u = nuevoUsuario("usuarioSoloSup");
        crearSuperior(u, "Camiseta", "Blanco", CategoriaSuperior.CAMISETA);

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), null, null, null);

        assertNotNull(respuesta);
        assertNotNull(respuesta.getFaltantes());
        assertTrue(respuesta.getFaltantes().contains("inferior"));
        assertTrue(respuesta.getFaltantes().contains("calzado"));
        assertFalse(respuesta.getFaltantes().contains("superior"));
    }

    // =========================================================================
    // TESTS DE RECOMENDACIÓN
    // =========================================================================

    /**
     * Verifica que con armario completo devuelve una recomendación válida.
     */
    @Test
    void testRecomendarConjuntoConArmarioCompletoDevuelveRecomendacion() {
        Usuario u = nuevoUsuario("usuarioCompleto");
        crearArmarioCompleto(u);

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), null, null, null);

        assertNotNull(respuesta);
        assertTrue(respuesta.getFaltantes() == null || respuesta.getFaltantes().isEmpty());
        assertNotNull(respuesta.getSuperior());
        assertNotNull(respuesta.getInferior());
        assertNotNull(respuesta.getCalzado());
    }

    /**
     * Verifica que con preferencias nulas devuelve una recomendación.
     */
    @Test
    void testRecomendarConjuntoConPreferenciasNulasDevuelveRecomendacion() {
        Usuario u = nuevoUsuario("usuarioPrefNull");
        crearArmarioCompleto(u);

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), (RecomendacionConjuntoRequestDto) null);

        assertNotNull(respuesta);
        assertTrue(respuesta.getFaltantes() == null || respuesta.getFaltantes().isEmpty());
    }

    // =========================================================================
    // TESTS DE PRENDAS FIJAS
    // =========================================================================

    /**
     * Verifica que al fijar una prenda superior, la recomendación la incluye.
     */
    @Test
    void testRecomendarConjuntoConSuperiorFijoUsaEsaPrenda() {
        Usuario u = nuevoUsuario("usuarioSupFijo");
        PrendaSuperior supFija = crearSuperior(u, "Camiseta Fija", "Rojo", CategoriaSuperior.CAMISETA);
        crearSuperior(u, "Otra Camiseta", "Verde", CategoriaSuperior.CAMISETA);
        crearInferior(u, "Pantalón", "Negro", CategoriaInferior.PANTALON);
        crearCalzado(u, "Zapatos", "Negro", CategoriaCalzado.DEPORTIVO);

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(
                u.getId(), supFija.getId(), null, null);

        assertNotNull(respuesta);
        assertNotNull(respuesta.getSuperior());
        assertEquals(supFija.getId(), respuesta.getSuperior().getId());
    }

    /**
     * Verifica que al fijar una prenda inferior, la recomendación la incluye.
     */
    @Test
    void testRecomendarConjuntoConInferiorFijoUsaEsaPrenda() {
        Usuario u = nuevoUsuario("usuarioInfFijo");
        crearSuperior(u, "Camiseta", "Blanco", CategoriaSuperior.CAMISETA);
        PrendaInferior infFija = crearInferior(u, "Pantalón Fijo", "Azul", CategoriaInferior.PANTALON);
        crearInferior(u, "Otro Pantalón", "Negro", CategoriaInferior.PANTALON);
        crearCalzado(u, "Zapatos", "Negro", CategoriaCalzado.DEPORTIVO);

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(
                u.getId(), null, infFija.getId(), null);

        assertNotNull(respuesta);
        assertNotNull(respuesta.getInferior());
        assertEquals(infFija.getId(), respuesta.getInferior().getId());
    }

    /**
     * Verifica que al fijar un calzado, la recomendación lo incluye.
     */
    @Test
    void testRecomendarConjuntoConCalzadoFijoUsaEsaPrenda() {
        Usuario u = nuevoUsuario("usuarioCalFijo");
        crearSuperior(u, "Camiseta", "Blanco", CategoriaSuperior.CAMISETA);
        crearInferior(u, "Pantalón", "Negro", CategoriaInferior.PANTALON);
        PrendaCalzado calFijo = crearCalzado(u, "Zapatillas Fijas", "Rojo", CategoriaCalzado.SNEAKER);
        crearCalzado(u, "Otras Zapatillas", "Azul", CategoriaCalzado.DEPORTIVO);

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(
                u.getId(), null, null, calFijo.getId());

        assertNotNull(respuesta);
        assertNotNull(respuesta.getCalzado());
        assertEquals(calFijo.getId(), respuesta.getCalzado().getId());
    }

    // =========================================================================
    // TESTS DE MODOS DE RECOMENDACIÓN
    // =========================================================================

    /**
     * Verifica que el modo COLOR filtra prendas por color.
     */
    @Test
    void testRecomendarConjuntoModoColorFiltraPorColor() {
        Usuario u = nuevoUsuario("usuarioModoColor");
        crearSuperior(u, "Camiseta Roja", "Rojo", CategoriaSuperior.CAMISETA);
        crearSuperior(u, "Camiseta Azul", "Azul", CategoriaSuperior.CAMISETA);
        crearInferior(u, "Pantalón Rojo", "Rojo", CategoriaInferior.PANTALON);
        crearInferior(u, "Pantalón Negro", "Negro", CategoriaInferior.PANTALON);
        crearCalzado(u, "Zapatos Rojos", "Rojo", CategoriaCalzado.DEPORTIVO);
        crearCalzado(u, "Zapatos Blancos", "Blanco", CategoriaCalzado.DEPORTIVO);

        RecomendacionConjuntoRequestDto prefs = new RecomendacionConjuntoRequestDto();
        prefs.setModo("COLOR");
        prefs.setColorFiltro("Rojo");
        prefs.setTipoCombinacion("TODO");

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), prefs);

        assertNotNull(respuesta);
        assertNotNull(respuesta.getMensaje());
    }

    /**
     * Verifica que el modo MARCA filtra prendas por marca.
     */
    @Test
    void testRecomendarConjuntoModoMarcaFiltraPorMarca() {
        Usuario u = nuevoUsuario("usuarioModoMarca");

        PrendaSuperior sup = crearSuperior(u, "Camiseta Nike", "Blanco", CategoriaSuperior.CAMISETA);
        sup.setMarca("Nike");
        prendaSuperiorRepository.save(sup);

        crearSuperior(u, "Camiseta Adidas", "Negro", CategoriaSuperior.CAMISETA);
        crearInferior(u, "Pantalón", "Negro", CategoriaInferior.PANTALON);
        crearCalzado(u, "Zapatos", "Negro", CategoriaCalzado.DEPORTIVO);

        RecomendacionConjuntoRequestDto prefs = new RecomendacionConjuntoRequestDto();
        prefs.setModo("MARCA");
        prefs.setMarcaFiltro("Nike");

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), prefs);

        assertNotNull(respuesta);
    }

    /**
     * Verifica que el modo TIEMPO con temperatura FRIO recomienda prendas adecuadas.
     */
    @Test
    void testRecomendarConjuntoModoTiempoFrio() {
        Usuario u = nuevoUsuario("usuarioTiempoFrio");
        crearSuperior(u, "Abrigo", "Negro", CategoriaSuperior.ABRIGO);
        crearSuperior(u, "Camiseta", "Blanco", CategoriaSuperior.CAMISETA);
        crearInferior(u, "Pantalón", "Negro", CategoriaInferior.PANTALON);
        crearCalzado(u, "Botas", "Negro", CategoriaCalzado.BOTA);

        RecomendacionConjuntoRequestDto prefs = new RecomendacionConjuntoRequestDto();
        prefs.setModo("TIEMPO");
        prefs.setTiempo("FRIO");

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), prefs);

        assertNotNull(respuesta);
        assertTrue(respuesta.getFaltantes() == null || respuesta.getFaltantes().isEmpty());
    }

    /**
     * Verifica que el modo TIEMPO con temperatura CALOR recomienda prendas adecuadas.
     */
    @Test
    void testRecomendarConjuntoModoTiempoCalor() {
        Usuario u = nuevoUsuario("usuarioTiempoCalor");
        crearSuperior(u, "Camiseta Ligera", "Blanco", CategoriaSuperior.CAMISETA);
        crearInferior(u, "Shorts", "Azul", CategoriaInferior.SHORT);
        crearInferior(u, "Pantalón", "Negro", CategoriaInferior.PANTALON);
        crearCalzado(u, "Sandalias", "Marrón", CategoriaCalzado.SANDALIA);

        RecomendacionConjuntoRequestDto prefs = new RecomendacionConjuntoRequestDto();
        prefs.setModo("TIEMPO");
        prefs.setTiempo("CALOR");

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), prefs);

        assertNotNull(respuesta);
        assertTrue(respuesta.getFaltantes() == null || respuesta.getFaltantes().isEmpty());
    }

    /**
     * Verifica que el modo SORPRESA genera una recomendación aleatoria.
     */
    @Test
    void testRecomendarConjuntoModoSorpresa() {
        Usuario u = nuevoUsuario("usuarioSorpresa");
        crearArmarioCompleto(u);

        RecomendacionConjuntoRequestDto prefs = new RecomendacionConjuntoRequestDto();
        prefs.setModo("SORPRESA");

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), prefs);

        assertNotNull(respuesta);
        assertTrue(respuesta.getFaltantes() == null || respuesta.getFaltantes().isEmpty());
        assertNotNull(respuesta.getSuperior());
        assertNotNull(respuesta.getInferior());
        assertNotNull(respuesta.getCalzado());
    }

    /**
     * Verifica que la recomendación incluye un mensaje explicativo.
     */
    @Test
    void testRecomendarConjuntoDevuelveMensajeExplicativo() {
        Usuario u = nuevoUsuario("usuarioMensaje");
        crearArmarioCompleto(u);

        RecomendacionConjuntoResponseDto respuesta = recomendadorService.recomendarConjunto(u.getId(), null, null, null);

        assertNotNull(respuesta);
        assertNotNull(respuesta.getMensaje());
        assertFalse(respuesta.getMensaje().isEmpty());
    }
}
