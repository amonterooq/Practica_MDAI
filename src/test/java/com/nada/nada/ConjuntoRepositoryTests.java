package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class ConjuntoRepositoryTests {

    @Autowired
    private TestEntityManager em;

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

    private Usuario nuevoUsuario(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@mail.com");
        return usuarioRepository.save(u);
    }

    private PrendaSuperior sup(Usuario u) {
        PrendaSuperior p = new PrendaSuperior();
        p.setNombre("Camiseta");
        p.setColor("Blanco");
        p.setMarca("M");
        p.setUsuario(u);
        p.setTalla("M");
        p.setUrlImagen("u");
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setManga(Manga.CORTA);
        return prendaSuperiorRepository.save(p);
    }

    private PrendaInferior inf(Usuario u) {
        PrendaInferior p = new PrendaInferior();
        p.setNombre("Pantalon");
        p.setColor("Negro");
        p.setMarca("M");
        p.setUsuario(u);
        p.setTalla("32");
        p.setUrlImagen("u");
        p.setCategoriaInferior(CategoriaInferior.PANTALON);
        return prendaInferiorRepository.save(p);
    }

    private PrendaCalzado cal(Usuario u) {
        PrendaCalzado p = new PrendaCalzado();
        p.setNombre("Zapatilla");
        p.setColor("Azul");
        p.setMarca("M");
        p.setUsuario(u);
        p.setTalla("42");
        p.setUrlImagen("u");
        p.setCategoria(CategoriaCalzado.DEPORTIVO);
        return prendaCalzadoRepository.save(p);
    }

    @Test
    void guardarYRecuperarConjuntoCompleto() {
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

        em.flush();
        em.clear();

        Conjunto rec = conjuntoRepository.findById(guardado.getId()).orElseThrow();
        assertEquals("Look Diario", rec.getNombre());
        assertEquals("eva", rec.getUsuario().getUsername());
        assertEquals(ps.getId(), rec.getPrendaSuperior().getId());
        assertEquals(pi.getId(), rec.getPrendaInferior().getId());
        assertEquals(pc.getId(), rec.getPrendaCalzado().getId());

        // regla de negocio: todas las prendas del conjunto pertenecen al mismo usuario
        assertEquals(rec.getUsuario().getId(), rec.getPrendaSuperior().getUsuario().getId());
        assertEquals(rec.getUsuario().getId(), rec.getPrendaInferior().getUsuario().getId());
        assertEquals(rec.getUsuario().getId(), rec.getPrendaCalzado().getUsuario().getId());
    }

    @Test
    void eliminarConjuntoNoEliminaPrendas() {
        Usuario u = nuevoUsuario("max");
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

        em.flush();
        conjuntoRepository.deleteById(c.getId());
        em.flush();
        em.clear();

        assertTrue(prendaSuperiorRepository.findById(ps.getId()).isPresent());
        assertTrue(prendaInferiorRepository.findById(pi.getId()).isPresent());
        assertTrue(prendaCalzadoRepository.findById(pc.getId()).isPresent());
    }

    @org.junit.jupiter.api.Test
    void permitirReutilizarPrendaEnVariosConjuntos() {
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

        em.flush();
        em.clear();

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

    @org.junit.jupiter.api.Test
    void actualizarConjunto_cambiaPrendasYDescripcion() {
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

        em.flush();
        em.clear();

        Conjunto loaded = conjuntoRepository.findById(c.getId()).orElseThrow();
        loaded.setDescripcion("new");
        loaded.setPrendaSuperior(ps2);
        loaded.setPrendaInferior(pi2);
        loaded.setPrendaCalzado(pc2);
        conjuntoRepository.save(loaded);

        em.flush();
        em.clear();

        Conjunto after = conjuntoRepository.findById(c.getId()).orElseThrow();
        assertEquals("new", after.getDescripcion());
        assertEquals(ps2.getId(), after.getPrendaSuperior().getId());
        assertEquals(pi2.getId(), after.getPrendaInferior().getId());
        assertEquals(pc2.getId(), after.getPrendaCalzado().getId());
    }

    @org.junit.jupiter.api.Test
    void guardarConjuntoSinUsuarioDebeFallar() {
        Usuario u = nuevoUsuario("nouser");
        PrendaSuperior ps = sup(u);

        Conjunto c = new Conjunto();
        c.setNombre("Sin usuario");
        c.setDescripcion("X");
        // sin c.setUsuario(...)
        c.setPrendaSuperior(ps);

        assertThatThrownBy(() -> {
            conjuntoRepository.save(c);
            em.flush(); // fuerza la constraint NOT NULL
        }).isInstanceOfAny(
                org.springframework.dao.DataIntegrityViolationException.class,
                IllegalArgumentException.class
        );
    }

    @org.junit.jupiter.api.Test
    void eliminarConjuntoNoEliminaUsuario() {
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

        em.flush();
        conjuntoRepository.deleteById(c.getId());
        em.flush();
        em.clear();

        assertTrue(usuarioRepository.findById(u.getId()).isPresent());
    }

    @org.junit.jupiter.api.Test
    void descripcionNoDebeExceder256Caracteres() {
        Usuario u = nuevoUsuario("limite-desc");
        Conjunto c = new Conjunto();
        c.setUsuario(u);
        c.setNombre("Desc larga");
        c.setDescripcion("x".repeat(257)); // > 256

        assertThatThrownBy(() -> {
            conjuntoRepository.save(c);
            em.flush(); // fuerza la validación/constraint en BD
        }).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
