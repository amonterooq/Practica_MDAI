package com.nada.nada.repository;

import com.nada.nada.data.model.*;
import com.nada.nada.data.model.enums.CategoriaCalzado;
import com.nada.nada.data.model.enums.CategoriaInferior;
import com.nada.nada.data.model.enums.CategoriaSuperior;
import com.nada.nada.data.model.enums.Manga;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para PostRepository.
 */
@SpringBootTest
@ActiveProfiles("test")
class PostRepositoryTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ConjuntoRepository conjuntoRepository;

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

        return conjuntoRepository.save(c);
    }

    /**
     * Verifica que se puede guardar un post y buscarlo por ID.
     */
    @Test
    void testGuardarYBuscarPostPorId() {
        Usuario u = nuevoUsuario("usuarioPostRepo");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        Post guardado = postRepository.save(post);

        assertNotNull(guardado.getId());

        Post recuperado = postRepository.findById(guardado.getId().longValue());
        assertNotNull(recuperado);
        assertEquals(u.getId(), recuperado.getUsuario().getId());
        assertEquals(c.getId(), recuperado.getConjunto().getId());
    }

    /**
     * Verifica el método legacy findById(long) que devuelve Post directamente.
     */
    @Test
    void testFindByIdLegacyDevuelvePost() {
        Usuario u = nuevoUsuario("usuarioPostLegacy");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        Post guardado = postRepository.save(post);

        Post recuperado = postRepository.findById(guardado.getId().longValue());
        assertNotNull(recuperado);
        assertEquals(guardado.getId(), recuperado.getId());
    }

    /**
     * Verifica que findAllWithLikes devuelve todos los posts con sus likes cargados.
     */
    @Test
    void testFindAllWithLikesDevuelveListaDePosts() {
        Usuario u = nuevoUsuario("usuarioPostAll");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        postRepository.save(post);

        List<Post> todos = postRepository.findAllWithLikes();
        assertNotNull(todos);
        assertTrue(todos.size() >= 1);
    }

    /**
     * Verifica que findByIdWithLikes carga correctamente la lista de usuarios que dieron like.
     */
    @Test
    void testFindByIdWithLikesDevuelvePostConLikes() {
        Usuario u = nuevoUsuario("usuarioPostLikes");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        Post guardado = postRepository.save(post);

        Optional<Post> recuperado = postRepository.findByIdWithLikes(guardado.getId());
        assertTrue(recuperado.isPresent());
        assertNotNull(recuperado.get().getUsuariosQueDieronLike());
    }

    /**
     * Verifica que se puede eliminar un post correctamente.
     */
    @Test
    @Transactional
    void testEliminarPostLoEliminaDeLaBaseDeDatos() {
        Usuario u = nuevoUsuario("usuarioPostElim");
        Conjunto c = crearConjunto(u);

        Post post = new Post(u, c);
        Post guardado = postRepository.save(post);
        Long id = guardado.getId();

        // Verificar que existe
        assertNotNull(postRepository.findById(id.longValue()));

        // Eliminar el post
        postRepository.delete(guardado);

        // Verificar que ya no existe
        assertNull(postRepository.findById(id.longValue()));
    }

    /**
     * Verifica que buscar un post inexistente devuelve null.
     */
    @Test
    void testBuscarPostInexistenteDevuelveEmpty() {
        Post resultado = postRepository.findById(999999L);
        assertNull(resultado);
    }

    /**
     * Verifica que findByIdWithLikes devuelve Optional vacío para posts inexistentes.
     */
    @Test
    void testFindByIdWithLikesPostInexistenteDevuelveEmpty() {
        Optional<Post> resultado = postRepository.findByIdWithLikes(999999L);
        assertTrue(resultado.isEmpty());
    }
}
