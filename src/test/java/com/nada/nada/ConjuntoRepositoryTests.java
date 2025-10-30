package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.ConjuntoRepository;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ConjuntoRepositoryTests {

    @Autowired UsuarioRepository usuarioRepository;
    @Autowired PrendaRepository prendaRepository;
    @Autowired ConjuntoRepository conjuntoRepository;
    @Autowired EntityManager em;

    private PrendaSuperior sup(Usuario u) { PrendaSuperior p = new PrendaSuperior(); p.setUsuario(u); p.setCategoria(CategoriaSuperior.CAMISETA); p.setColor("Azul"); p.setMarca("Zara"); p.setTalla("M"); return p; }
    private PrendaInferior inf(Usuario u) { PrendaInferior p = new PrendaInferior(); p.setUsuario(u); p.setCategoria(CategoriaInferior.PANTALON); p.setColor("Blanco"); p.setMarca("Levis"); p.setTalla("40"); return p; }
    private PrendaCalzado calz(Usuario u) { PrendaCalzado p = new PrendaCalzado(); p.setUsuario(u); p.setCategoria(CategoriaCalzado.SANDALIAS); p.setColor("Negro"); p.setMarca("Nike"); p.setTalla("42"); return p; }

    @Test
    @Transactional
    void crearConjuntoConTresPrendasDelMismoUsuario() {
        Usuario u = new Usuario("Creador", "nada", "nada@gmail.com");
        usuarioRepository.save(u);

        PrendaSuperior ps = prendaRepository.save(sup(u));
        PrendaInferior pi = prendaRepository.save(inf(u));
        PrendaCalzado pc = prendaRepository.save(calz(u));
        prendaRepository.flush();

        Conjunto cj = new Conjunto();
        cj.setUsuario(u);
        cj.setPrendaSuperior(ps);
        cj.setPrendaInferior(pi);
        cj.setPrendaCalzado(pc);
        cj.setDescripcion("Reunión");
        conjuntoRepository.save(cj);
        em.clear();

        Conjunto fetched = conjuntoRepository.findById(cj.getId()).orElseThrow();
        assertThat(fetched.getUsuario().getId()).isEqualTo(u.getId());
        assertThat(fetched.getPrendaSuperior().getUsuario().getId()).isEqualTo(u.getId());
        assertThat(fetched.getPrendaInferior().getUsuario().getId()).isEqualTo(u.getId());
        assertThat(fetched.getPrendaCalzado().getUsuario().getId()).isEqualTo(u.getId());
    }

    @Test
    void notaNoDebeExceder256Caracteres() {
        Usuario u = new Usuario("Límite Nota", "pwd", "limite@example.com");
        usuarioRepository.save(u);

        Conjunto cj = new Conjunto();
        cj.setUsuario(u);
        cj.setDescripcion("x".repeat(257)); // > 256

        assertThatThrownBy(() -> {
            conjuntoRepository.save(cj);
            em.flush();
        }).isInstanceOfAny(ConstraintViolationException.class, org.springframework.dao.DataIntegrityViolationException.class);
    }

    // java
    @Test
    @Transactional
    void noPermitirPrendasDeOtroUsuarioEnConjunto() {
        Usuario u1 = new Usuario("U1", "pwd1", "u1@example.com");
        Usuario u2 = new Usuario("U2", "pwd2", "u2@example.com");
        usuarioRepository.save(u1);
        usuarioRepository.save(u2);

        PrendaSuperior ps = prendaRepository.save(sup(u1)); // pertenece a u1
        PrendaInferior pi = prendaRepository.save(inf(u2)); // pertenece a u2 (distinto usuario)
        PrendaCalzado pc = prendaRepository.save(calz(u1));
        // forzar sincronización si el repo no expone flush; usamos em.flush() en la aserción más abajo

        Conjunto cj = new Conjunto();
        cj.setUsuario(u1);
        cj.setPrendaSuperior(ps);
        cj.setPrendaInferior(pi);  // inválido: distinta pertenencia
        cj.setPrendaCalzado(pc);
        cj.setNota("Invalid");

        assertThatThrownBy(() -> {
            conjuntoRepository.save(cj);
            em.flush();
        }).isInstanceOfAny(IllegalArgumentException.class, org.springframework.dao.DataIntegrityViolationException.class);
    }

}
