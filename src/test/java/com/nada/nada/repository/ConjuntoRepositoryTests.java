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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para ConjuntoRepository.
 */
@SpringBootTest
@ActiveProfiles("test")
class ConjuntoRepositoryTests {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrendaSuperiorRepository prendaSuperiorRepository;

    @Autowired
    private PrendaInferiorRepository prendaInferiorRepository;

    @Autowired
    private PrendaCalzadoRepository prendaCalzadoRepository;

    @Autowired
    private ConjuntoRepository conjuntoRepository;

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
        u.setEmail(username + "@gmail.com");
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
        p.setNombre("Camiseta");
        p.setColor("Blanco");
        p.setMarca("M");
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
        p.setNombre("Pantalon");
        p.setColor("Negro");
        p.setMarca("M");
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
        p.setNombre("Zapatilla");
        p.setColor("Azul");
        p.setMarca("M");
        p.setUsuario(u);
        p.setTalla("42");
        p.setDirImagen("images/calz.png");
        p.setCategoria(CategoriaCalzado.DEPORTIVO);
        return prendaCalzadoRepository.save(p);
    }

    /**
     * Verifica que los datos iniciales se cargan correctamente desde data.sql.
     */
    @Test
    void testComprobarDatosIniciales() {
        Conjunto c = conjuntoRepository.findByNombre("Negocios");
        assertNotNull(c);
        assertEquals("Negocios", c.getNombre());
    }

    /**
     * Verifica que se puede guardar un conjunto completo y recuperarlo con todas sus relaciones.
     */
    @Test
    void testGuardarYRecuperarConjuntoCompleto() {
        Usuario u = nuevoUsuario("eva");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Look Diario");
        c.setDescripcion("Conjunto diario");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        Conjunto guardado = conjuntoRepository.save(c);

        Conjunto rec = conjuntoRepository.findById(guardado.getId()).orElseThrow();
        assertEquals("Look Diario", rec.getNombre());
        assertEquals("eva", rec.getUsuario().getUsername());
        assertEquals(ps.getId(), rec.getPrendaSuperior().getId());
        assertEquals(pi.getId(), rec.getPrendaInferior().getId());
        assertEquals(pc.getId(), rec.getPrendaCalzado().getId());

        assertEquals(rec.getUsuario().getId(), rec.getPrendaSuperior().getUsuario().getId());
        assertEquals(rec.getUsuario().getId(), rec.getPrendaInferior().getUsuario().getId());
        assertEquals(rec.getUsuario().getId(), rec.getPrendaCalzado().getUsuario().getId());
    }

    /**
     * Verifica que una misma prenda puede ser reutilizada en varios conjuntos.
     */
    @Test
    void testPermitirReutilizarPrendaEnVariosConjuntos() {
        Usuario u = nuevoUsuario("reuse");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        long antes = conjuntoRepository.count();

        Conjunto c1 = new Conjunto();
        c1.setNombre("Look 1");
        c1.setDescripcion("Día 1");
        c1.setUsuario(u);
        c1.setPrendaSuperior(ps);
        c1.setPrendaInferior(pi);
        c1.setPrendaCalzado(pc);
        conjuntoRepository.save(c1);

        Conjunto c2 = new Conjunto();
        c2.setNombre("Look 2");
        c2.setDescripcion("Día 2");
        c2.setUsuario(u);
        c2.setPrendaSuperior(ps);
        c2.setPrendaInferior(pi);
        c2.setPrendaCalzado(pc);
        conjuntoRepository.save(c2);

        Conjunto r1 = conjuntoRepository.findById(c1.getId()).orElseThrow();
        Conjunto r2 = conjuntoRepository.findById(c2.getId()).orElseThrow();

        assertEquals(ps.getId(), r1.getPrendaSuperior().getId());
        assertEquals(ps.getId(), r2.getPrendaSuperior().getId());
        assertEquals(pi.getId(), r1.getPrendaInferior().getId());
        assertEquals(pi.getId(), r2.getPrendaInferior().getId());
        assertEquals(pc.getId(), r1.getPrendaCalzado().getId());
        assertEquals(pc.getId(), r2.getPrendaCalzado().getId());
        assertEquals(antes + 2, conjuntoRepository.count());
    }

    /**
     * Verifica que se pueden actualizar las prendas y descripción de un conjunto.
     */
    @Test
    void testActualizarConjuntoCambiaPrendasYDescripcion() {
        Usuario u = nuevoUsuario("upd");
        PrendaSuperior ps1 = sup(u);
        PrendaInferior pi1 = inf(u);
        PrendaCalzado pc1 = cal(u);
        PrendaSuperior ps2 = sup(u);
        PrendaInferior pi2 = inf(u);
        PrendaCalzado pc2 = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Inicial");
        c.setDescripcion("old");
        c.setUsuario(u);
        c.setPrendaSuperior(ps1);
        c.setPrendaInferior(pi1);
        c.setPrendaCalzado(pc1);
        c = conjuntoRepository.save(c);

        Conjunto loaded = conjuntoRepository.findById(c.getId()).orElseThrow();
        loaded.setDescripcion("new");
        loaded.setPrendaSuperior(ps2);
        loaded.setPrendaInferior(pi2);
        loaded.setPrendaCalzado(pc2);
        conjuntoRepository.save(loaded);

        Conjunto after = conjuntoRepository.findById(c.getId()).orElseThrow();
        assertEquals("new", after.getDescripcion());
        assertEquals(ps2.getId(), after.getPrendaSuperior().getId());
        assertEquals(pi2.getId(), after.getPrendaInferior().getId());
        assertEquals(pc2.getId(), after.getPrendaCalzado().getId());
    }

    /**
     * Verifica que no se puede guardar un conjunto sin usuario asociado.
     */
    @Test
    void testGuardarConjuntoSinUsuarioDebeFallar() {
        Usuario u = nuevoUsuario("nouser");
        PrendaSuperior ps = sup(u);

        Conjunto c = new Conjunto();
        c.setNombre("Sin usuario");
        c.setDescripcion("X");
        c.setPrendaSuperior(ps);

        assertThatThrownBy(() -> {
            conjuntoRepository.save(c);
        }).isInstanceOfAny(
                org.springframework.dao.DataIntegrityViolationException.class,
                IllegalArgumentException.class
        );
    }

    @Test
    void testEliminarConjuntoNoEliminaUsuario() {
        Usuario u = nuevoUsuario("keepuser");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Look");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        c = conjuntoRepository.save(c);

        conjuntoRepository.deleteById(c.getId());

        assertTrue(usuarioRepository.findById(u.getId()).isPresent());
    }
}
