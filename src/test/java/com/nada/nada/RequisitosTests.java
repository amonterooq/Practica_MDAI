package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RequisitosTests {

    @Autowired private TestEntityManager em;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PrendaSuperiorRepository prendaSuperiorRepository;
    @Autowired private PrendaInferiorRepository prendaInferiorRepository;
    @Autowired private PrendaCalzadoRepository prendaCalzadoRepository;
    @Autowired private ConjuntoRepository conjuntoRepository;

    private Usuario user(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@mail.com");
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
    void eliminarUsuarioEliminaSusPrendasYConjuntos() {
        Usuario u = user("cascade");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Set");
        c.setDescripcion("full");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        conjuntoRepository.save(c);

        em.flush();
        em.clear();

        Long uid = u.getId();
        Long psId = ps.getId();
        Long piId = pi.getId();
        Long pcId = pc.getId();
        Long cId = c.getId();

        usuarioRepository.deleteById(uid);
        em.flush();
        em.clear();

        assertTrue(usuarioRepository.findById(uid).isEmpty());
        assertTrue(prendaSuperiorRepository.findById(psId).isEmpty());
        assertTrue(prendaInferiorRepository.findById(piId).isEmpty());
        assertTrue(prendaCalzadoRepository.findById(pcId).isEmpty());
        assertTrue(conjuntoRepository.findById(cId).isEmpty());
    }
}
