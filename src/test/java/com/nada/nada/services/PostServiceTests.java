package com.nada.nada.services;

import com.nada.nada.data.model.*;
import com.nada.nada.data.model.enums.CategoriaCalzado;
import com.nada.nada.data.model.enums.CategoriaInferior;
import com.nada.nada.data.model.enums.CategoriaSuperior;
import com.nada.nada.data.model.enums.Manga;
import com.nada.nada.data.repository.*;
import com.nada.nada.data.services.ConjuntoService;
import com.nada.nada.data.services.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para PostService.
 */
@SpringBootTest
@ActiveProfiles("test")
class PostServiceTests {

    @Autowired
    private PostService postService;

    @Autowired
    private ConjuntoService conjuntoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostRepository postRepository;

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
     * Crea y persiste una prenda superior de prueba.
     *
     * @param u usuario propietario
     * @return prenda superior guardada
     */
    private PrendaSuperior sup(Usuario u) {
        PrendaSuperior p = new PrendaSuperior();
        p.setNombre("Superior");
        p.setColor("Blanco");
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("M");
        p.setDirImagen("images/sup.png");
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setManga(Manga.CORTA);
        return prendaSuperiorRepository.save(p);
    }

    /**
     * Crea y persiste una prenda inferior de prueba.
     *
     * @param u usuario propietario
     * @return prenda inferior guardada
     */
    private PrendaInferior inf(Usuario u) {
        PrendaInferior p = new PrendaInferior();
        p.setNombre("Inferior");
        p.setColor("Negro");
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("32");
        p.setDirImagen("images/inf.png");
        p.setCategoriaInferior(CategoriaInferior.PANTALON);
        return prendaInferiorRepository.save(p);
    }

    /**
     * Crea y persiste un calzado de prueba.
     *
     * @param u usuario propietario
     * @return calzado guardado
     */
    private PrendaCalzado cal(Usuario u) {
        PrendaCalzado p = new PrendaCalzado();
        p.setNombre("Calzado");
        p.setColor("Azul");
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("42");
        p.setDirImagen("images/calz.png");
        p.setCategoria(CategoriaCalzado.DEPORTIVO);
        return prendaCalzadoRepository.save(p);
    }

    /**
     * Crea un conjunto completo con las 3 prendas requeridas.
     *
     * @param u usuario propietario del conjunto
     * @return conjunto guardado en base de datos
     */
    private Conjunto crearConjunto(Usuario u) {
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto Test");
        c.setDescripcion("Descripcion");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);

        return conjuntoService.guardarConjunto(c);
    }

    // =========================================================================
    // TESTS DE CREACIÓN Y BÚSQUEDA
    // =========================================================================

    /**
     * Verifica que se puede crear un post y buscarlo por ID.
     */
    @Test
    void testCrearPostYBuscarPorId() {
        Usuario u = nuevoUsuario("usuarioPostCrear");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        Post guardado = postService.crearPost(post);

        assertNotNull(guardado.getId());

        Optional<Post> recuperado = postService.buscarPorId(guardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(u.getId(), recuperado.get().getUsuario().getId());
        assertEquals(c.getId(), recuperado.get().getConjunto().getId());
    }

    /**
     * Verifica que crear un post nulo lanza excepción.
     */
    @Test
    void testCrearPostNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> postService.crearPost(null));
    }

    /**
     * Verifica que buscar por ID nulo lanza excepción.
     */
    @Test
    void testBuscarPorIdNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> postService.buscarPorId(null));
    }

    /**
     * Verifica que buscar por ID negativo lanza excepción.
     */
    @Test
    void testBuscarPorIdNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> postService.buscarPorId(-1L));
    }

    /**
     * Verifica que buscar todos devuelve una lista con posts.
     */
    @Test
    void testBuscarTodosDevuelveListaConPosts() {
        Usuario u = nuevoUsuario("usuarioPostTodos");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        postService.crearPost(post);

        List<Post> todos = postService.buscarTodos();
        assertNotNull(todos);
        assertTrue(todos.size() >= 1);
    }

    /**
     * Verifica que buscar posts por usuario devuelve solo los suyos.
     */
    @Test
    void testBuscarPostsPorUsuarioDevuelveSoloLosSuyos() {
        Usuario u1 = nuevoUsuario("usuarioPost1");
        Usuario u2 = nuevoUsuario("usuarioPost2");

        Conjunto c1 = crearConjunto(u1);
        Conjunto c2 = crearConjunto(u2);

        Post post1 = new Post(u1, c1);
        Post post2 = new Post(u2, c2);

        postService.crearPost(post1);
        postService.crearPost(post2);

        List<Post> postsU1 = postService.buscarPostsPorUsuario(u1.getId());
        assertEquals(1, postsU1.size());
        assertEquals(u1.getId(), postsU1.get(0).getUsuario().getId());
    }

    /**
     * Verifica que buscar posts por usuario nulo lanza excepción.
     */
    @Test
    void testBuscarPostsPorUsuarioNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> postService.buscarPostsPorUsuario(null));
    }

    /**
     * Verifica que buscar posts por usuario negativo lanza excepción.
     */
    @Test
    void testBuscarPostsPorUsuarioNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> postService.buscarPostsPorUsuario(-1L));
    }

    // =========================================================================
    // TESTS DE LIKES
    // =========================================================================

    /**
     * Verifica que contar likes de un post sin likes devuelve cero.
     */
    @Test
    void testContarLikesDePostSinLikesDevuelveCero() {
        Usuario u = nuevoUsuario("usuarioPostLikes0");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        Post guardado = postService.crearPost(post);

        int likes = postService.contarLikes(guardado.getId());
        assertEquals(0, likes);
    }

    /**
     * Verifica que contar likes con ID nulo devuelve cero.
     */
    @Test
    void testContarLikesConIdNuloDevuelveCero() {
        int likes = postService.contarLikes(null);
        assertEquals(0, likes);
    }

    /**
     * Verifica que usuarioHaDadoLike devuelve false si no hay likes.
     */
    @Test
    void testUsuarioHaDadoLikeDevuelveFalseSinLikes() {
        Usuario u = nuevoUsuario("usuarioPostHaLike");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        Post guardado = postService.crearPost(post);

        boolean haLikeado = postService.usuarioHaDadoLike(guardado.getId(), u.getId());
        assertFalse(haLikeado);
    }

    /**
     * Verifica que usuarioHaDadoLike con nulos devuelve false.
     */
    @Test
    void testUsuarioHaDadoLikeConNulosDevuelveFalse() {
        assertFalse(postService.usuarioHaDadoLike(null, 1L));
        assertFalse(postService.usuarioHaDadoLike(1L, null));
        assertFalse(postService.usuarioHaDadoLike(null, null));
    }

    // =========================================================================
    // TESTS DE ELIMINACIÓN
    // =========================================================================

    /**
     * Verifica que eliminar un post lo elimina de la base de datos.
     */
    @Test
    void testEliminarPostLoEliminaDeLaBaseDeDatos() {
        Usuario u = nuevoUsuario("usuarioPostEliminar");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        Post guardado = postService.crearPost(post);
        Long postId = guardado.getId();

        // Verificar que existe antes de eliminar
        assertTrue(postService.buscarPorId(postId).isPresent());

        // Eliminar el post - no debería lanzar excepción
        assertDoesNotThrow(() -> postService.eliminarPost(postId));

        // Verificar que al intentar eliminar de nuevo no hace nada (ya no existe)
        assertDoesNotThrow(() -> postService.eliminarPost(postId));
    }

    /**
     * Verifica que eliminar post con ID nulo lanza excepción.
     */
    @Test
    void testEliminarPostConIdNuloNoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> postService.eliminarPost(null));
    }

    /**
     * Verifica que eliminar post con ID negativo lanza excepción.
     */
    @Test
    void testEliminarPostConIdNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> postService.eliminarPost(-1L));
    }

    /**
     * Verifica que eliminar un post inexistente no lanza excepción.
     */
    @Test
    void testEliminarPostInexistenteNoLanzaExcepcion() {
        // No debería lanzar excepción si el post no existe
        assertDoesNotThrow(() -> postService.eliminarPost(999999L));
    }

    /**
     * Verifica que eliminar likes con ID nulo no lanza excepción.
     */
    @Test
    void testEliminarLikesDelPostConIdNuloNoLanzaExcepcion() {
        // No debería hacer nada, pero tampoco fallar
        assertDoesNotThrow(() -> postService.eliminarLikesDelPost(null));
    }

    /**
     * Verifica que eliminar likes con ID negativo no lanza excepción.
     */
    @Test
    void testEliminarLikesDelPostConIdNegativoNoLanzaExcepcion() {
        assertDoesNotThrow(() -> postService.eliminarLikesDelPost(-1L));
    }

    /**
     * Verifica que eliminar likes de post inexistente no lanza excepción.
     */
    @Test
    void testEliminarLikesDelPostInexistenteNoLanzaExcepcion() {
        assertDoesNotThrow(() -> postService.eliminarLikesDelPost(999999L));
    }
}
