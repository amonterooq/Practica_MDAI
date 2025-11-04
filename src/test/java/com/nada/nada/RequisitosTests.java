package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RequisitosTests {

    @Autowired TestEntityManager em;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired PrendaSuperiorRepository prendaSuperiorRepository;
    @Autowired PrendaInferiorRepository prendaInferiorRepository;
    @Autowired PrendaCalzadoRepository prendaCalzadoRepository;
    @Autowired ConjuntoRepository conjuntoRepository;

    private Usuario u(String name) {
        Usuario u = new Usuario();
        u.setUsername(name);
        u.setPassword("pwd");
        u.setEmail(name + "g@mail.com");
        return usuarioRepository.save(u);
    }
    private PrendaSuperior sup(Usuario u) {
        PrendaSuperior p = new PrendaSuperior();
        p.setNombre("Sup");
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
        p.setNombre("Inf");
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
        p.setNombre("Cal");
        p.setColor("Azul");
        p.setMarca("M");
        p.setUsuario(u);
        p.setTalla("42");
        p.setUrlImagen("u");
        p.setCategoria(CategoriaCalzado.DEPORTIVO);
        return prendaCalzadoRepository.save(p);
    }

    @Test
    void descripcionLongitudMaxima_256_OK() {
        Usuario u = u("len-ok");
        Conjunto c = new Conjunto();
        c.setNombre("Max");
        c.setUsuario(u);
        c.setDescripcion("x".repeat(256));
        conjuntoRepository.save(c);
        em.flush();
        em.clear();

        Conjunto rec = conjuntoRepository.findById(c.getId()).orElseThrow();
        assertThat(rec.getDescripcion()).hasSize(256);
    }

    @Test
    void noPermitirEliminarPrendaUsadaEnConjunto() {
        Usuario u = u("fk-guard");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Look");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        conjuntoRepository.save(c);
        em.flush();

        prendaSuperiorRepository.deleteById(ps.getId());
        assertThatThrownBy(() -> em.flush())
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Transactional
    void borrarUsuario_EliminaPrendasYConjuntos() {
        Usuario u = u("cascade-del");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Look");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        conjuntoRepository.save(c);
        em.flush();

        usuarioRepository.deleteById(u.getId());
        em.flush();
        em.clear();

        assertThat(usuarioRepository.findById(u.getId())).isEmpty();
        assertThat(prendaSuperiorRepository.findById(ps.getId())).isEmpty();
        assertThat(prendaInferiorRepository.findById(pi.getId())).isEmpty();
        assertThat(prendaCalzadoRepository.findById(pc.getId())).isEmpty();
        assertThat(conjuntoRepository.findById(c.getId())).isEmpty();
    }
}
